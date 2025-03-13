package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.dto.AccountBalanceDTO;
import com.lennartmoeller.finance.projection.AccountBalanceProjection;
import com.lennartmoeller.finance.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountBalanceService {

	private final AccountRepository accountRepository;

	public List<AccountBalanceDTO> findAll() {
		return accountRepository.getAccountBalances().stream()
			.sorted(Comparator.comparing(AccountBalanceProjection::getTransactionCount).reversed())
			.map(accountBalanceProjection -> {
				AccountBalanceDTO accountBalanceDTO = new AccountBalanceDTO();
				accountBalanceDTO.setAccountId(accountBalanceProjection.getAccountId());
				accountBalanceDTO.setBalance(accountBalanceProjection.getBalance());
				return accountBalanceDTO;
			})
			.toList();
	}

}
