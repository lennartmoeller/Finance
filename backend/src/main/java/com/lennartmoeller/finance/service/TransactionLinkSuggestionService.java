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
import java.util.List;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionLinkSuggestionService {
    private static final int WINDOW_DAYS = 7;
    private final TransactionLinkSuggestionRepository repository;
    private final TransactionLinkSuggestionMapper mapper;
    private final BankTransactionRepository bankTransactionRepository;
    private final TransactionRepository transactionRepository;

    public List<TransactionLinkSuggestionDTO> findAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
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
                            .filter(t -> isNotExistingSuggestion(existing, bankTransaction, t))
                            .map(t -> {
                                long daysBetween = Math.abs(
                                        new DateRange(bankTransaction.getBookingDate(), t.getDate()).getDays() - 1);
                                double probability = 1.0 - daysBetween / (2.0 * WINDOW_DAYS);
                                TransactionLinkSuggestion suggestion = new TransactionLinkSuggestion();
                                suggestion.setBankTransaction(bankTransaction);
                                suggestion.setTransaction(t);
                                suggestion.setProbability(probability);
                                suggestion.setLinkState(
                                        probability == 1.0
                                                ? TransactionLinkState.AUTO_CONFIRMED
                                                : TransactionLinkState.UNDECIDED);
                                return suggestion;
                            });
                })
                .toList();

        List<TransactionLinkSuggestion> saved = suggestions.isEmpty() ? List.of() : repository.saveAll(suggestions);

        return saved.stream().map(mapper::toDto).toList();
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
        generateSuggestions(transactions, null);
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
        generateSuggestions(null, bankTransactions);
    }

    public void removeForTransaction(Long id) {
        repository.deleteAllByTransaction_Id(id);
    }

    public void removeForBankTransaction(Long id) {
        repository.deleteAllByBankTransaction_Id(id);
    }

    private boolean isNotExistingSuggestion(
            List<TransactionLinkSuggestion> existing, BankTransaction bankTransaction, Transaction transaction) {
        return existing.stream()
                .noneMatch(s -> s.getBankTransaction().getId().equals(bankTransaction.getId())
                        && s.getTransaction().getId().equals(transaction.getId()));
    }
}
