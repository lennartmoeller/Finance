package com.lennartmoeller.finance.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

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
