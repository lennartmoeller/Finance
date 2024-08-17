package com.lennartmoeller.finance.controller;

import com.lennartmoeller.finance.dto.AccountDTO;
import com.lennartmoeller.finance.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;

	@GetMapping
	public List<AccountDTO> getAccounts() throws InterruptedException {
		Thread.sleep(5000); // TODO: Testing purposes only, remove this line
		return accountService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
		return accountService.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public AccountDTO createOrUpdateAccount(@RequestBody AccountDTO accountDTO) {
		Optional<AccountDTO> optionalAccountDTO = Optional.ofNullable(accountDTO.getId()).flatMap(accountService::findById);
		if (optionalAccountDTO.isEmpty()) {
			accountDTO.setId(null);
		}
		return accountService.save(accountDTO);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
		accountService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

}
