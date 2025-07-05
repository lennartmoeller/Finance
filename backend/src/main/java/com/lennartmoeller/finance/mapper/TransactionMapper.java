package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.TransactionDTO;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.CategoryRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
        componentModel = "spring",
        uses = {AccountMapper.class, CategoryMapper.class},
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface TransactionMapper {
    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "category.id", target = "categoryId")
    TransactionDTO toDto(Transaction transaction);

    @Mapping(target = "account", source = "accountId", qualifiedByName = "mapAccountIdToAccount")
    @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapCategoryIdToCategory")
    Transaction toEntity(
            TransactionDTO dto,
            @Context AccountRepository accountRepository,
            @Context CategoryRepository categoryRepository);

    @Named("mapAccountIdToAccount")
    default Account mapAccountIdToAccount(Long accountId, @Context AccountRepository repository) {
        return accountId != null ? repository.findById(accountId).orElse(null) : null;
    }

    @Named("mapAccountToAccountId")
    default Long mapAccountToAccountId(Account account) {
        return account != null ? account.getId() : null;
    }

    @Named("mapCategoryIdToCategory")
    default Category mapCategoryIdToCategory(Long categoryId, @Context CategoryRepository repository) {
        return categoryId != null ? repository.findById(categoryId).orElse(null) : null;
    }

    @Named("mapCategoryToCategoryId")
    default Long mapCategoryToCategoryId(Category category) {
        return category != null ? category.getId() : null;
    }
}
