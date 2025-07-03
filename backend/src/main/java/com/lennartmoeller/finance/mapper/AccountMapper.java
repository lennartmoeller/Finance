package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.AccountDTO;
import com.lennartmoeller.finance.model.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface AccountMapper {

    AccountDTO toDto(Account account);

    Account toEntity(AccountDTO accountDTO);
}
