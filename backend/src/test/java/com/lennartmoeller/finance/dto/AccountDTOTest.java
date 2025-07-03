package com.lennartmoeller.finance.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AccountDTOTest {
    @Test
    void gettersAndSetters() {
        AccountDTO dto = new AccountDTO();
        dto.setId(3L);
        dto.setLabel("Checking");
        dto.setIban("DE123");
        dto.setStartBalance(200L);
        dto.setActive(true);
        dto.setDeposits(false);

        assertEquals(3L, dto.getId());
        assertEquals("Checking", dto.getLabel());
        assertEquals("DE123", dto.getIban());
        assertEquals(200L, dto.getStartBalance());
        assertTrue(dto.getActive());
        assertFalse(dto.getDeposits());
    }
}
