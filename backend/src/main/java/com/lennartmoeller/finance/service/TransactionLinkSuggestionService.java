package com.lennartmoeller.finance.service;

import static com.lennartmoeller.finance.model.TransactionLinkState.AUTO_CONFIRMED;
import static com.lennartmoeller.finance.model.TransactionLinkState.AUTO_REJECTED;
import static com.lennartmoeller.finance.model.TransactionLinkState.CONFIRMED;
import static com.lennartmoeller.finance.model.TransactionLinkState.UNDECIDED;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import com.lennartmoeller.finance.dto.TransactionLinkSuggestionDTO;
import com.lennartmoeller.finance.mapper.TransactionLinkSuggestionMapper;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.model.TransactionLinkState;
import com.lennartmoeller.finance.model.TransactionLinkSuggestion;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import com.lennartmoeller.finance.repository.TransactionLinkSuggestionRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import com.lennartmoeller.finance.util.ImmutablePairUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionLinkSuggestionService {
    private final BankTransactionRepository bankTransactionRepository;
    private final TransactionLinkSuggestionMapper mapper;
    private final TransactionLinkSuggestionRepository repository;
    private final TransactionRepository transactionRepository;

    public List<TransactionLinkSuggestionDTO> findAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    public Optional<TransactionLinkSuggestionDTO> findById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    public TransactionLinkSuggestionDTO updateLinkState(Long id, TransactionLinkState linkState) {
        if (List.of(AUTO_CONFIRMED, AUTO_REJECTED).contains(linkState)) {
            throw new IllegalArgumentException("Cannot set link state to " + linkState + " manually.");
        }

        TransactionLinkSuggestion existing = repository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No suggestion found with id: " + id));

        if (existing.getLinkState() == linkState) {
            return mapper.toDto(existing);
        }

        List<Long> btIds = List.of(existing.getBankTransaction().getId());
        List<Long> tIds = List.of(existing.getTransaction().getId());

        if (linkState == CONFIRMED) {
            // all other confirmed suggestions for the same bank transaction or transaction must be auto-rejected
            repository.findAllByBankTransactionIdsOrTransactionIds(btIds, tIds).stream()
                    .filter(s -> s.getLinkState() == CONFIRMED)
                    .forEach(s -> {
                        s.setLinkState(AUTO_REJECTED);
                        repository.save(s);
                    });
        }

        existing.setLinkState(linkState);
        TransactionLinkSuggestion saved = repository.save(existing);

        updateLinkStateFor(btIds, tIds);

        return mapper.toDto(saved);
    }

    public List<TransactionLinkSuggestionDTO> updateAllFor(
            List<BankTransaction> bankTransactions, List<Transaction> transactions) {
        List<BankTransaction> bts = Optional.ofNullable(bankTransactions).orElse(List.of());
        List<Transaction> ts = Optional.ofNullable(transactions).orElse(List.of());
        if (bts.isEmpty() && ts.isEmpty()) {
            return List.of();
        }

        List<TransactionLinkSuggestion> existingList = repository.findAllByBankTransactionIdsOrTransactionIds(
                bts.stream().map(BankTransaction::getId).toList(),
                ts.stream().map(Transaction::getId).toList());

        List<TransactionLinkSuggestion> updatedList = generateAllFor(bts, ts);

        List<TransactionLinkSuggestion> toSave = new ArrayList<>();
        for (TransactionLinkSuggestion updated : updatedList) {
            int idx = existingList.indexOf(updated);
            if (idx < 0) {
                toSave.add(updated);
            } else {
                TransactionLinkSuggestion existing = existingList.remove(idx);
                boolean hasNoMatchingLinkState = existing.getLinkState() != updated.getLinkState();
                boolean hasNoMatchingProbability = !Objects.equals(existing.getProbability(), updated.getProbability());
                if (existing.hasManualLinkStateDecision()) {
                    if (hasNoMatchingProbability) {
                        existing.setProbability(updated.getProbability());
                        toSave.add(existing);
                    }
                } else if (hasNoMatchingLinkState || hasNoMatchingProbability) {
                    existing.setLinkState(updated.getLinkState());
                    existing.setProbability(updated.getProbability());
                    toSave.add(existing);
                }
            }
        }

        repository.saveAll(toSave);
        repository.deleteAll(existingList);

        updateLinkStateFor(
                bts.stream().map(BankTransaction::getId).toList(),
                ts.stream().map(Transaction::getId).toList());

        return toSave.stream().map(mapper::toDto).toList();
    }

    public void removeForTransaction(Long id) {
        List<Long> bankTransactionIds =
                repository.findAllByBankTransactionIdsOrTransactionIds(null, List.of(id)).stream()
                        .map(s -> s.getBankTransaction().getId())
                        .distinct()
                        .toList();
        repository.deleteAllByTransaction_Id(id);
        updateLinkStateFor(bankTransactionIds, null);
    }

    private void updateLinkStateFor(@Nullable List<Long> bankTransactionIds, @Nullable List<Long> transactionIds) {
        List<Long> bts = Optional.ofNullable(bankTransactionIds).orElse(List.of());
        List<Long> ts = Optional.ofNullable(transactionIds).orElse(List.of());
        if (bts.isEmpty() && ts.isEmpty()) {
            return;
        }

        List<TransactionLinkSuggestion> suggestions = repository.findAllByBankTransactionIdsOrTransactionIds(bts, ts);
        if (suggestions.isEmpty()) {
            return;
        }

        Map<TransactionLinkSuggestion, TransactionLinkState> original =
                suggestions.stream().collect(toMap(s -> s, TransactionLinkSuggestion::getLinkState));

        suggestions.stream().filter(s -> s.getLinkState() == AUTO_REJECTED).forEach(s -> s.setLinkState(UNDECIDED));

        Consumer<Collection<TransactionLinkSuggestion>> adjustLinkState = group -> {
            Map<TransactionLinkState, List<TransactionLinkSuggestion>> byState =
                    group.stream().collect(Collectors.groupingBy(TransactionLinkSuggestion::getLinkState));
            List<TransactionLinkSuggestion> confirmed = byState.getOrDefault(CONFIRMED, List.of());
            List<TransactionLinkSuggestion> autoConfirmed = byState.getOrDefault(AUTO_CONFIRMED, List.of());
            List<TransactionLinkSuggestion> undecided = byState.getOrDefault(UNDECIDED, List.of());

            if (confirmed.size() == 1) {
                Stream.concat(autoConfirmed.stream(), undecided.stream()).forEach(s -> s.setLinkState(AUTO_REJECTED));
            } else if (autoConfirmed.size() == 1) {
                Stream.concat(confirmed.stream(), undecided.stream()).forEach(s -> s.setLinkState(AUTO_REJECTED));
            } else {
                Stream.of(confirmed, autoConfirmed).forEach(list -> {
                    if (list.size() > 1) {
                        list.forEach(s -> s.setLinkState(UNDECIDED));
                    }
                });
            }
        };

        suggestions.stream()
                .filter(s -> bts.contains(s.getBankTransaction().getId()))
                .collect(groupingBy(s -> s.getBankTransaction().getId()))
                .values()
                .forEach(adjustLinkState);

        suggestions.stream()
                .filter(s -> ts.contains(s.getTransaction().getId()))
                .collect(groupingBy(s -> s.getTransaction().getId()))
                .values()
                .forEach(adjustLinkState);

        List<TransactionLinkSuggestion> toSave = suggestions.stream()
                .filter(s -> original.get(s) != s.getLinkState())
                .toList();

        if (!toSave.isEmpty()) {
            repository.saveAll(toSave);
        }
    }

    private List<TransactionLinkSuggestion> generateAllFor(
            @Nullable List<BankTransaction> bankTransactions, @Nullable List<Transaction> transactions) {
        List<BankTransaction> bts = bankTransactions == null ? List.of() : bankTransactions;
        List<Transaction> ts = transactions == null ? List.of() : transactions;

        List<BankTransaction> allBts = ts.isEmpty() ? List.of() : bankTransactionRepository.findAll();
        List<Transaction> allTs = bts.isEmpty() ? List.of() : transactionRepository.findAll();

        Stream<ImmutablePair<BankTransaction, Transaction>> stream1 = ImmutablePairUtils.crossProductStream(bts, allTs);
        Stream<ImmutablePair<BankTransaction, Transaction>> stream2 = ImmutablePairUtils.crossProductStream(allBts, ts);
        return Stream.concat(stream1, stream2)
                .map(pair -> TransactionLinkSuggestion.of(pair.getLeft(), pair.getRight()))
                .filter(TransactionLinkSuggestion::isUseful)
                .distinct()
                .toList();
    }
}
