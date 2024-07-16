package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;

	public List<Account> findAll() {
		return accountRepository.findAll();
	}

	public Optional<Account> findById(Long id) {
		return accountRepository.findById(id);
	}

	public Account save(Account account) {
		return accountRepository.save(account);
	}

	public void deleteById(Long id) {
		accountRepository.deleteById(id);
	}

}
