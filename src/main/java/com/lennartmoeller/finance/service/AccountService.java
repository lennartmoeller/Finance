package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.dto.AccountDTO;
import com.lennartmoeller.finance.mapper.AccountMapper;
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
	private final AccountMapper accountMapper;

	public List<AccountDTO> findAll() {
		return accountRepository.findAll().stream().map(accountMapper::toDto).toList();
	}

	public Optional<AccountDTO> findById(Long id) {
		return accountRepository.findById(id).map(accountMapper::toDto);
	}

	public AccountDTO save(AccountDTO accountDTO) {
		Account account = accountMapper.toEntity(accountDTO);
		Account savedAccount = accountRepository.save(account);
		return accountMapper.toDto(savedAccount);
	}

	public void deleteById(Long id) {
		accountRepository.deleteById(id);
	}

}
