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

        return bankTransactionList.stream()
                .flatMap(bankTransaction -> {
                    LocalDate date = bankTransaction.getBookingDate();
                    LocalDate start = date.minusDays(7);
                    LocalDate end = date.plusDays(7);

                    return transactionList.stream()
                            .filter(t -> t.getAccount().equals(bankTransaction.getAccount()))
                            .filter(t -> t.getAmount().equals(bankTransaction.getAmount()))
                            .filter(t ->
                                    !t.getDate().isBefore(start) && !t.getDate().isAfter(end))
                            .filter(t -> !repository.existsByBankTransactionAndTransaction(bankTransaction, t))
                            .map(t -> {
                                long daysBetween = Math.abs(
                                        new DateRange(bankTransaction.getBookingDate(), t.getDate()).getDays() - 1);
                                double probability = 1.0 - (daysBetween / 7.0);
                                TransactionLinkSuggestion suggestion = new TransactionLinkSuggestion();
                                suggestion.setBankTransaction(bankTransaction);
                                suggestion.setTransaction(t);
                                suggestion.setProbability(probability);
                                suggestion.setLinkState(TransactionLinkState.UNDECIDED);
                                return repository.save(suggestion);
                            });
                })
                .map(mapper::toDto)
                .toList();
    }
}
