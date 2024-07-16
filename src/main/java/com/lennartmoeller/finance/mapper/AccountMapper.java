package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.AccountDTO;
import com.lennartmoeller.finance.model.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

	public AccountDTO toDto(Account account) {
		return new AccountDTO(
			account.getId(),
			account.getLabel(),
			account.getStartBalance(),
			account.getActive()
		);
	}

	public Account toEntity(AccountDTO accountDTO) {
		return new Account(
			accountDTO.getId(),
			accountDTO.getLabel(),
			accountDTO.getStartBalance(),
			accountDTO.getActive()
		);
	}
}
