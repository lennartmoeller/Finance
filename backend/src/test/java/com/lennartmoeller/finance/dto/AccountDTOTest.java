package com.lennartmoeller.finance.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountDTOTest {
	@Test
	void gettersAndSetters() {
		AccountDTO dto = new AccountDTO();
		dto.setId(3L);
		dto.setLabel("Checking");
		dto.setStartBalance(200L);
		dto.setActive(true);

		assertEquals(3L, dto.getId());
		assertEquals("Checking", dto.getLabel());
		assertEquals(200L, dto.getStartBalance());
		assertTrue(dto.getActive());
	}
}
