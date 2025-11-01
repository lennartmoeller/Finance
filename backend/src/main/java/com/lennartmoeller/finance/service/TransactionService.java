package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.dto.TransactionDTO;
import com.lennartmoeller.finance.mapper.TransactionMapper;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.CategoryRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionLinkSuggestionService suggestionService;
    private final TransactionMapper transactionMapper;
    private final TransactionRepository transactionRepository;

    public List<TransactionDTO> findAll() {
        return transactionRepository.findAll().stream()
                .sorted(Comparator.comparing(Transaction::getDate).thenComparing(Transaction::getId))
                .map(transactionMapper::toDto)
                .toList();
    }

    public Optional<TransactionDTO> findById(Long id) {
        return transactionRepository.findById(id).map(transactionMapper::toDto);
    }

    public TransactionDTO save(TransactionDTO transactionDTO) {
        Transaction transaction = transactionMapper.toEntity(transactionDTO, accountRepository, categoryRepository);
        Transaction savedTransaction = transactionRepository.save(transaction);
        suggestionService.updateAllFor(null, List.of(savedTransaction));
        return transactionMapper.toDto(savedTransaction);
    }

    public void deleteById(Long id) {
        suggestionService.removeForTransaction(id);
        transactionRepository.deleteById(id);
    }
}
