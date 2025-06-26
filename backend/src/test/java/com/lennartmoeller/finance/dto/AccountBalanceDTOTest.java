package com.lennartmoeller.finance.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountBalanceDTOTest {
    @Test
    void gettersAndSetters() {
        AccountBalanceDTO dto = new AccountBalanceDTO();
        dto.setAccountId(5L);
        dto.setBalance(100L);

        assertEquals(5L, dto.getAccountId());
        assertEquals(100L, dto.getBalance());
    }
}
