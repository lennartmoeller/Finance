package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.dto.TransactionDTO;
import com.lennartmoeller.finance.mapper.TransactionMapper;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

	private final TransactionRepository transactionRepository;
	private final TransactionMapper transactionMapper;

	public List<TransactionDTO> findAll(YearMonth yearMonth) {
		List<Transaction> transactions;
		if (yearMonth != null) {
			transactions = transactionRepository.findAllByYearMonth(yearMonth.getYear(), yearMonth.getMonthValue());
		} else {
			transactions = transactionRepository.findAll();
		}
		return transactions.stream().map(transactionMapper::toDto).toList();
	}

	public Optional<TransactionDTO> findById(Long id) {
		return transactionRepository.findById(id).map(transactionMapper::toDto);
	}

	public TransactionDTO save(TransactionDTO transactionDTO) {
		Transaction transaction = transactionMapper.toEntity(transactionDTO);
		Transaction savedTransaction = transactionRepository.save(transaction);
		return transactionMapper.toDto(savedTransaction);
	}

	public void deleteById(Long id) {
		transactionRepository.deleteById(id);
	}

}
