package com.lennartmoeller.finance.controller;

import com.lennartmoeller.finance.dto.AccountDTO;
import com.lennartmoeller.finance.mapper.AccountMapper;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;
	private final AccountMapper accountMapper;

	@GetMapping
	public Map<Long, AccountDTO> getAllAccounts() {
		return accountService.findAll().stream()
			.map(accountMapper::toDto)
			.collect(Collectors.toMap(AccountDTO::getId, account -> account));
	}

	@GetMapping("/{id}")
	public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
		return accountService.findById(id)
			.map(accountMapper::toDto)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public AccountDTO createOrUpdateAccount(@RequestBody AccountDTO accountDTO) {
		Optional<Account> optionalAccount = Optional.ofNullable(accountDTO.getId()).flatMap(accountService::findById);
		if (optionalAccount.isEmpty()) {
			accountDTO.setId(null);
		}
		return accountMapper.toDto(accountService.save(accountMapper.toEntity(accountDTO)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
		accountService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

}
