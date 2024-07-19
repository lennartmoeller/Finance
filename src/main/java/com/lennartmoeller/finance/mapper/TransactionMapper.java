package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.TransactionDTO;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.CategoryRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class TransactionMapper {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Mapping(source = "account.id", target = "accountId")
	@Mapping(source = "category.id", target = "categoryId")
	public abstract TransactionDTO toDto(Transaction transaction);

	@Mapping(target = "account", source = "accountId")
	@Mapping(target = "category", source = "categoryId")
	public abstract Transaction toEntity(TransactionDTO transactionDTO);

	Long mapAccountToAccountId(Account account) {
		return account != null ? account.getId() : null;
	}

	Account mapAccountIdToAccount(Long accountId) {
		return accountId != null ? accountRepository.findById(accountId).orElse(null) : null;
	}

	Long mapCategoryToCategoryId(Category category) {
		return category != null ? category.getId() : null;
	}

	Category mapCategoryIdToCategory(Long categoryId) {
		return categoryId != null ? categoryRepository.findById(categoryId).orElse(null) : null;
	}

}
