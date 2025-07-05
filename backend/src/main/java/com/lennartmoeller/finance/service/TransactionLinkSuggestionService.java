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

    public List<TransactionLinkSuggestionDTO> generateSuggestions() {
        List<TransactionLinkSuggestion> savedSuggestions = new ArrayList<>();
        List<BankTransaction> bankTransactions = bankTransactionRepository.findAll();
        for (BankTransaction bankTransaction : bankTransactions) {
            LocalDate date = bankTransaction.getBookingDate();
            LocalDate start = date.minusDays(7);
            LocalDate end = date.plusDays(7);
            List<Transaction> matches = transactionRepository.findAllByAccountAndAmountAndDateBetween(
                    bankTransaction.getAccount(), bankTransaction.getAmount(), start, end);
            int candidateCount = matches.size();
            for (Transaction transaction : matches) {
                if (repository.existsByBankTransactionAndTransaction(bankTransaction, transaction)) {
                    continue;
                }
                long daysBetween =
                        Math.abs(new DateRange(bankTransaction.getBookingDate(), transaction.getDate()).getDays() - 1);
                double base = 1.0 - (daysBetween / 7.0);
                double probability = base / candidateCount;
                TransactionLinkSuggestion suggestion = new TransactionLinkSuggestion();
                suggestion.setBankTransaction(bankTransaction);
                suggestion.setTransaction(transaction);
                suggestion.setProbability(probability);
                suggestion.setLinkState(TransactionLinkState.UNDECIDED);
                TransactionLinkSuggestion persisted = repository.save(suggestion);
                savedSuggestions.add(persisted);
            }
        }
        return savedSuggestions.stream().map(mapper::toDto).toList();
    }
}
