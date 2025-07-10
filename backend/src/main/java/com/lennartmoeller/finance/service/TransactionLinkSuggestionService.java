package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.dto.TransactionLinkSuggestionDTO;
import com.lennartmoeller.finance.mapper.TransactionLinkSuggestionMapper;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.model.TransactionLinkState;
import com.lennartmoeller.finance.model.TransactionLinkSuggestion;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import com.lennartmoeller.finance.repository.TransactionLinkSuggestionRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import com.lennartmoeller.finance.util.DateRange;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionLinkSuggestionService {
    private static final int WINDOW_DAYS = 7;
    private final BankTransactionRepository bankTransactionRepository;
    private final TransactionLinkSuggestionMapper mapper;
    private final TransactionLinkSuggestionRepository repository;
    private final TransactionRepository transactionRepository;

    private static boolean isConfirmed(TransactionLinkSuggestion suggestion) {
        TransactionLinkState state = suggestion.getLinkState();
        return state == TransactionLinkState.CONFIRMED || state == TransactionLinkState.AUTO_CONFIRMED;
    }

    private static TransactionLinkState defaultState(TransactionLinkSuggestion s) {
        return s.getProbability() == 1.0 ? TransactionLinkState.AUTO_CONFIRMED : TransactionLinkState.UNDECIDED;
    }

    private void ensureConsistency(@Nullable List<Long> bankTransactionIds, @Nullable List<Long> transactionIds) {
        boolean noBankIds = bankTransactionIds == null || bankTransactionIds.isEmpty();
        boolean noTxIds = transactionIds == null || transactionIds.isEmpty();
        if (noBankIds && noTxIds) {
            return;
        }

        List<TransactionLinkSuggestion> suggestions =
                repository.findAllByBankTransactionIdsOrTransactionIds(bankTransactionIds, transactionIds);
        if (suggestions.isEmpty()) {
            return;
        }

        Set<Long> confirmedBanks = suggestions.stream()
                .filter(TransactionLinkSuggestionService::isConfirmed)
                .map(s -> s.getBankTransaction().getId())
                .collect(java.util.stream.Collectors.toSet());
        Set<Long> confirmedTxs = suggestions.stream()
                .filter(TransactionLinkSuggestionService::isConfirmed)
                .map(s -> s.getTransaction().getId())
                .collect(java.util.stream.Collectors.toSet());

        List<TransactionLinkSuggestion> toSave = new ArrayList<>();
        for (TransactionLinkSuggestion s : suggestions) {
            boolean hasConfirmed =
                    confirmedBanks.contains(s.getBankTransaction().getId())
                            || confirmedTxs.contains(s.getTransaction().getId());
            TransactionLinkState desired = s.getLinkState();
            if (hasConfirmed) {
                if (!isConfirmed(s)) {
                    desired = TransactionLinkState.AUTO_REJECTED;
                }
            } else if (s.getLinkState() == TransactionLinkState.AUTO_REJECTED) {
                desired = defaultState(s);
            }

            if (desired != s.getLinkState()) {
                s.setLinkState(desired);
                toSave.add(s);
            }
        }

        if (!toSave.isEmpty()) {
            repository.saveAll(toSave);
        }
    }

    public List<TransactionLinkSuggestionDTO> findAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    public Optional<TransactionLinkSuggestionDTO> findById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    public List<TransactionLinkSuggestionDTO> generateSuggestions(
            @Nullable List<Transaction> transactions, @Nullable List<BankTransaction> bankTransactions) {
        List<Transaction> transactionList = transactions != null ? transactions : transactionRepository.findAll();
        List<BankTransaction> bankTransactionList =
                bankTransactions != null ? bankTransactions : bankTransactionRepository.findAll();

        List<Long> bankIds =
                bankTransactionList.stream().map(BankTransaction::getId).toList();
        List<Long> transactionIds =
                transactionList.stream().map(Transaction::getId).toList();
        List<TransactionLinkSuggestion> existing =
                repository.findAllByBankTransactionIdsAndTransactionIds(bankIds, transactionIds);

        List<TransactionLinkSuggestion> suggestions = bankTransactionList.stream()
                .flatMap(bankTransaction -> {
                    LocalDate date = bankTransaction.getBookingDate();
                    DateRange range = new DateRange(date.minusDays(WINDOW_DAYS), date.plusDays(WINDOW_DAYS));
                    return transactionList.stream()
                            .filter(t -> t.getAccount()
                                    .getId()
                                    .equals(bankTransaction.getAccount().getId()))
                            .filter(t -> t.getAmount().equals(bankTransaction.getAmount()))
                            .filter(t -> new DateRange(t.getDate()).getOverlapDays(range) != 0)
                            .filter(t -> existing.stream()
                                    .noneMatch(s -> s.getBankTransaction()
                                                    .getId()
                                                    .equals(bankTransaction.getId())
                                            && s.getTransaction().getId().equals(t.getId())))
                            .map(t -> buildSuggestion(bankTransaction, t));
                })
                .toList();

        List<TransactionLinkSuggestion> saved = suggestions.isEmpty() ? List.of() : repository.saveAll(suggestions);
        List<Long> bIds = saved.stream()
                .map(s -> s.getBankTransaction().getId())
                .distinct()
                .toList();
        List<Long> tIds =
                saved.stream().map(s -> s.getTransaction().getId()).distinct().toList();
        ensureConsistency(bIds, tIds);
        return saved.stream().map(mapper::toDto).toList();
    }

    private static TransactionLinkSuggestion buildSuggestion(BankTransaction bankTransaction, Transaction transaction) {
        long daysBetween =
                Math.abs(new DateRange(bankTransaction.getBookingDate(), transaction.getDate()).getDays() - 1);
        double probability = 1.0 - daysBetween / (2.0 * WINDOW_DAYS);
        TransactionLinkSuggestion suggestion = new TransactionLinkSuggestion();
        suggestion.setBankTransaction(bankTransaction);
        suggestion.setTransaction(transaction);
        suggestion.setProbability(probability);
        suggestion.setLinkState(
                probability == 1.0 ? TransactionLinkState.AUTO_CONFIRMED : TransactionLinkState.UNDECIDED);
        return suggestion;
    }

    public void updateForTransactions(@Nullable List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return;
        }
        List<Long> ids = transactions.stream().map(Transaction::getId).toList();
        List<TransactionLinkSuggestion> existing = repository.findAllByBankTransactionIdsAndTransactionIds(null, ids);
        List<TransactionLinkSuggestion> deletions = existing.stream()
                .filter(s -> s.getLinkState() == TransactionLinkState.AUTO_CONFIRMED
                        || s.getLinkState() == TransactionLinkState.UNDECIDED)
                .toList();
        repository.deleteAll(deletions);
        List<Long> bankIds = deletions.stream()
                .map(s -> s.getBankTransaction().getId())
                .distinct()
                .toList();
        generateSuggestions(transactions, null);
        ensureConsistency(bankIds, ids);
    }

    public void updateForBankTransactions(@Nullable List<BankTransaction> bankTransactions) {
        if (bankTransactions == null || bankTransactions.isEmpty()) {
            return;
        }
        List<Long> ids = bankTransactions.stream().map(BankTransaction::getId).toList();
        List<TransactionLinkSuggestion> existing = repository.findAllByBankTransactionIdsAndTransactionIds(ids, null);
        List<TransactionLinkSuggestion> deletions = existing.stream()
                .filter(s -> s.getLinkState() == TransactionLinkState.AUTO_CONFIRMED
                        || s.getLinkState() == TransactionLinkState.UNDECIDED)
                .toList();
        repository.deleteAll(deletions);
        List<Long> txIds = deletions.stream()
                .map(s -> s.getTransaction().getId())
                .distinct()
                .toList();
        generateSuggestions(null, bankTransactions);
        ensureConsistency(ids, txIds);
    }

    public void removeForTransaction(Long id) {
        List<TransactionLinkSuggestion> affected =
                repository.findAllByBankTransactionIdsOrTransactionIds(null, List.of(id));
        repository.deleteAllByTransaction_Id(id);
        List<Long> bankIds = affected.stream()
                .map(s -> s.getBankTransaction().getId())
                .distinct()
                .toList();
        ensureConsistency(bankIds, List.of(id));
    }

    public void removeForBankTransaction(Long id) {
        List<TransactionLinkSuggestion> affected =
                repository.findAllByBankTransactionIdsOrTransactionIds(List.of(id), null);
        repository.deleteAllByBankTransaction_Id(id);
        List<Long> txIds = affected.stream()
                .map(s -> s.getTransaction().getId())
                .distinct()
                .toList();
        ensureConsistency(List.of(id), txIds);
    }

    public Optional<TransactionLinkSuggestionDTO> updateLinkState(Long id, TransactionLinkState linkState) {
        return repository
                .findById(id)
                .map(existing -> {
                    existing.setLinkState(linkState);
                    TransactionLinkSuggestion saved = repository.save(existing);
                    ensureConsistency(
                            List.of(saved.getBankTransaction().getId()),
                            List.of(saved.getTransaction().getId()));
                    return saved;
                })
                .map(mapper::toDto);
    }
}
