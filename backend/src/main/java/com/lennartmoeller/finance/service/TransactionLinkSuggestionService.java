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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
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

    public List<TransactionLinkSuggestionDTO> generateSuggestions(
            @Nullable List<Transaction> transactions, @Nullable List<BankTransaction> bankTransactions) {
        // if no transactions or bank transactions are provided, use all from the repository
        List<Transaction> transactionList = transactions != null ? transactions : transactionRepository.findAll();
        List<BankTransaction> bankTransactionList =
                bankTransactions != null ? bankTransactions : bankTransactionRepository.findAll();

        // get existing suggestions to avoid duplicates
        List<Long> bankIds =
                bankTransactionList.stream().map(BankTransaction::getId).toList();
        List<Long> transactionIds =
                transactionList.stream().map(Transaction::getId).toList();
        List<TransactionLinkSuggestion> existing =
                repository.findAllByBankTransactionIdsAndTransactionIds(bankIds, transactionIds);

        // filter suggestions
        List<TransactionLinkSuggestion> suggestions = bankTransactionList.stream()
                .flatMap(bankTransaction -> transactionList.stream()
                        .map(transaction -> TransactionLinkSuggestion.of(bankTransaction, transaction))
                        .filter(suggestion -> existing.stream().noneMatch(suggestion::equals))
                        .filter(TransactionLinkSuggestion::isUseful))
                .toList();

        // save suggestions
        List<TransactionLinkSuggestion> saved = suggestions.isEmpty() ? List.of() : repository.saveAll(suggestions);

        // ensure consistency of link states
        List<Long> savedBankTransactionIds = saved.stream()
                .map(s -> s.getBankTransaction().getId())
                .distinct()
                .toList();
        List<Long> savedTransactionIds =
                saved.stream().map(s -> s.getTransaction().getId()).distinct().toList();
        ensureLinkStateConsistency(savedBankTransactionIds, savedTransactionIds);

        return saved.stream().map(mapper::toDto).toList();
    }

    public void ensureLinkStateConsistency(
            @Nullable List<Long> bankTransactionIds, @Nullable List<Long> transactionIds) {
        boolean noBankTransactionIds = bankTransactionIds == null || bankTransactionIds.isEmpty();
        boolean noTransactionIds = transactionIds == null || transactionIds.isEmpty();
        if (noBankTransactionIds && noTransactionIds) {
            return;
        }

        List<TransactionLinkSuggestion> suggestions =
                repository.findAllByBankTransactionIdsOrTransactionIds(bankTransactionIds, transactionIds);
        if (suggestions.isEmpty()) {
            return;
        }

        Set<Long> confirmedBankTransactionIds = suggestions.stream()
                .filter(TransactionLinkSuggestion::isConfirmed)
                .map(s -> s.getBankTransaction().getId())
                .collect(Collectors.toSet());
        Set<Long> confirmedTransactionIds = suggestions.stream()
                .filter(TransactionLinkSuggestion::isConfirmed)
                .map(s -> s.getTransaction().getId())
                .collect(Collectors.toSet());

        for (TransactionLinkSuggestion suggestion : suggestions) {
            if (confirmedBankTransactionIds.contains(
                            suggestion.getBankTransaction().getId())
                    || confirmedTransactionIds.contains(
                            suggestion.getTransaction().getId())) {
                if (!suggestion.isConfirmed()) {
                    suggestion.setLinkState(TransactionLinkState.AUTO_REJECTED);
                    repository.save(suggestion);
                }
            } else if (suggestion.getLinkState() == TransactionLinkState.AUTO_REJECTED) {
                suggestion.setLinkState(suggestion.getDefaultLinkState());
                repository.save(suggestion);
            }
        }
    }

    public void updateForTransactions(@Nullable List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return;
        }
        List<Long> ids = transactions.stream().map(Transaction::getId).toList();
        List<TransactionLinkSuggestion> existing = repository.findAllByBankTransactionIdsOrTransactionIds(null, ids);
        List<TransactionLinkSuggestion> deletions = existing.stream()
                .filter(TransactionLinkSuggestion::hasNoManualLinkStateDecision)
                .toList();
        repository.deleteAll(deletions);
        generateSuggestions(transactions, null);
        List<Long> deletedBankTransactionIds = deletions.stream()
                .map(s -> s.getBankTransaction().getId())
                .distinct()
                .toList();
        ensureLinkStateConsistency(deletedBankTransactionIds, ids);
    }

    public void updateForBankTransactions(@Nullable List<BankTransaction> bankTransactions) {
        if (bankTransactions == null || bankTransactions.isEmpty()) {
            return;
        }
        List<Long> ids = bankTransactions.stream().map(BankTransaction::getId).toList();
        List<TransactionLinkSuggestion> existing = repository.findAllByBankTransactionIdsOrTransactionIds(ids, null);
        List<TransactionLinkSuggestion> deletions = existing.stream()
                .filter(TransactionLinkSuggestion::hasNoManualLinkStateDecision)
                .toList();
        repository.deleteAll(deletions);
        generateSuggestions(null, bankTransactions);
        List<Long> deletedTransactionIds = deletions.stream()
                .map(s -> s.getTransaction().getId())
                .distinct()
                .toList();
        ensureLinkStateConsistency(ids, deletedTransactionIds);
    }

    public void removeForTransaction(Long id) {
        List<Long> bankTransactionIds =
                repository.findAllByBankTransactionIdsOrTransactionIds(null, List.of(id)).stream()
                        .map(s -> s.getBankTransaction().getId())
                        .distinct()
                        .toList();
        repository.deleteAllByTransaction_Id(id);
        ensureLinkStateConsistency(bankTransactionIds, null);
    }

    public Optional<TransactionLinkSuggestionDTO> updateLinkState(Long id, TransactionLinkState linkState) {
        return repository
                .findById(id)
                .map(existing -> {
                    existing.setLinkState(linkState);
                    TransactionLinkSuggestion saved = repository.save(existing);
                    ensureLinkStateConsistency(
                            List.of(saved.getBankTransaction().getId()),
                            List.of(saved.getTransaction().getId()));
                    return saved;
                })
                .map(mapper::toDto);
    }
}
