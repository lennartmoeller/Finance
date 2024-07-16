package com.lennartmoeller.finance.service;

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

	public List<Transaction> findAll(YearMonth yearMonth) {
		if (yearMonth != null) {
			return transactionRepository.findAllByYearMonth(yearMonth.getYear(), yearMonth.getMonthValue());
		}
		return transactionRepository.findAll();
	}

	public Optional<Transaction> findById(Long id) {
		return transactionRepository.findById(id);
	}

	public Transaction save(Transaction transaction) {
		return transactionRepository.save(transaction);
	}

	public void deleteById(Long id) {
		transactionRepository.deleteById(id);
	}

}
