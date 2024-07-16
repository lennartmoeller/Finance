package com.lennartmoeller.finance.controller;

import com.lennartmoeller.finance.dto.TransactionDTO;
import com.lennartmoeller.finance.mapper.TransactionMapper;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

	private final TransactionService transactionService;
	private final TransactionMapper transactionMapper;

	@GetMapping
	public Map<Long, TransactionDTO> getAllTransactions(@RequestParam(required = false) YearMonth yearMonth) {
		return transactionService.findAll(yearMonth).stream()
			.map(transactionMapper::toDto)
			.collect(Collectors.toMap(TransactionDTO::getId, transaction -> transaction));
	}

	@GetMapping("/{id}")
	public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
		return transactionService.findById(id)
			.map(transactionMapper::toDto)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public TransactionDTO createOrUpdateTransaction(@RequestBody TransactionDTO transactionDTO) {
		Optional<Transaction> optionalTransaction = Optional.ofNullable(transactionDTO.getId()).flatMap(transactionService::findById);
		if (optionalTransaction.isEmpty()) {
			transactionDTO.setId(null);
		}
		return transactionMapper.toDto(transactionService.save(transactionMapper.toEntity(transactionDTO)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
		transactionService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

}
