package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.TransactionDTO;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.CategoryRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = "spring",
        uses = {AccountMapper.class, CategoryMapper.class})
public abstract class TransactionMapper {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "category.id", target = "categoryId")
    public abstract TransactionDTO toDto(Transaction transaction);

    @Mapping(target = "account", source = "accountId", qualifiedByName = "mapAccountIdToAccount")
    @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapCategoryIdToCategory")
    public abstract Transaction toEntity(TransactionDTO transactionDTO);

    @Named("mapAccountIdToAccount")
    Account mapAccountIdToAccount(Long accountId) {
        return accountId != null ? accountRepository.findById(accountId).orElse(null) : null;
    }

    @Named("mapAccountToAccountId")
    Long mapAccountToAccountId(Account account) {
        return account != null ? account.getId() : null;
    }

    @Named("mapCategoryIdToCategory")
    Category mapCategoryIdToCategory(Long categoryId) {
        return categoryId != null ? categoryRepository.findById(categoryId).orElse(null) : null;
    }

    @Named("mapCategoryToCategoryId")
    Long mapCategoryToCategoryId(Category category) {
        return category != null ? category.getId() : null;
    }
}
