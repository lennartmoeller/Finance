package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.AccountDTO;
import com.lennartmoeller.finance.model.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class AccountMapper {

    public abstract AccountDTO toDto(Account account);

    public abstract Account toEntity(AccountDTO accountDTO);
}
