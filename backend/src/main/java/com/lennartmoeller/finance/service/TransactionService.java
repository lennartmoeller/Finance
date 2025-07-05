package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.dto.TransactionDTO;
import com.lennartmoeller.finance.mapper.TransactionMapper;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import com.lennartmoeller.finance.repository.CategoryRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final CategoryService categoryService;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final BankTransactionRepository bankTransactionRepository;

    public List<TransactionDTO> findFiltered(
            @Nullable List<Long> accountIds,
            @Nullable List<Long> categoryIds,
            @Nullable List<YearMonth> yearMonths,
            @Nullable Boolean pinned) {
        List<Long> extendedCategoryIds = categoryService.collectChildCategoryIdsRecursively(categoryIds);

        List<String> yearMonthStrings = yearMonths == null
                ? null
                : yearMonths.stream().map(YearMonth::toString).toList();

        List<Transaction> transactions =
                transactionRepository.findFiltered(accountIds, extendedCategoryIds, yearMonthStrings, pinned);
        return transactions.stream()
                .sorted(Comparator.comparing(Transaction::getDate).thenComparing(Transaction::getId))
                .map(transactionMapper::toDto)
                .toList();
    }

    public Optional<TransactionDTO> findById(Long id) {
        return transactionRepository.findById(id).map(transactionMapper::toDto);
    }

    public TransactionDTO save(TransactionDTO transactionDTO) {
        Transaction transaction = transactionMapper.toEntity(
                transactionDTO, accountRepository, categoryRepository, bankTransactionRepository);
        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(savedTransaction);
    }

    public void deleteById(Long id) {
        transactionRepository.deleteById(id);
    }
}
