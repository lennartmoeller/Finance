package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.TransactionDTO;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.service.AccountService;
import com.lennartmoeller.finance.service.CategoryService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {AccountService.class, CategoryService.class})
public abstract class TransactionMapper {

	@Autowired
	protected AccountService accountService;

	@Autowired
	protected CategoryService categoryService;

	@Mapping(source = "account.id", target = "accountId")
	@Mapping(source = "category.id", target = "categoryId")
	public abstract TransactionDTO toDto(Transaction transaction);

	@Mapping(target = "account", expression = "java(accountService.findById(transactionDTO.getAccountId()).orElse(null))")
	@Mapping(target = "category", expression = "java(categoryService.findById(transactionDTO.getCategoryId()).orElse(null))")
	public abstract Transaction toEntity(TransactionDTO transactionDTO);

}
