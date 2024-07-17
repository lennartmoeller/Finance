package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.TransactionDTO;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.service.AccountService;
import com.lennartmoeller.finance.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

	private final AccountService accountService;
	private final CategoryService categoryService;

	public TransactionDTO toDto(Transaction transaction) {
		return new TransactionDTO(
			transaction.getId(),
			transaction.getAccount().getId(),
			transaction.getCategory().getId(),
			transaction.getDate(),
			transaction.getAmount(),
			transaction.getDescription()
		);
	}

	public Transaction toEntity(TransactionDTO transactionDTO) {
		Account account = accountService.findById(transactionDTO.getAccountId())
			.orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
		Category category = categoryService.findById(transactionDTO.getCategoryId())
			.orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

		return new Transaction(
			transactionDTO.getId(),
			account,
			category,
			transactionDTO.getDate(),
			transactionDTO.getAmount(),
			transactionDTO.getDescription()
		);
	}

}
