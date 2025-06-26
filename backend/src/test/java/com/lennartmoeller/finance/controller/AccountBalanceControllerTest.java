package com.lennartmoeller.finance.controller;

import com.lennartmoeller.finance.dto.AccountBalanceDTO;
import com.lennartmoeller.finance.service.AccountBalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AccountBalanceControllerTest {

	private AccountBalanceService service;
	private AccountBalanceController controller;

	@BeforeEach
	void setUp() {
		service = mock(AccountBalanceService.class);
		controller = new AccountBalanceController(service);
	}

	@Test
	void testGetAccountBalances() {
		List<AccountBalanceDTO> list = List.of(new AccountBalanceDTO(), new AccountBalanceDTO());
		when(service.findAll()).thenReturn(list);

		List<AccountBalanceDTO> result = controller.getAccountBalances();

		assertEquals(list, result);
		verify(service).findAll();
	}
}
