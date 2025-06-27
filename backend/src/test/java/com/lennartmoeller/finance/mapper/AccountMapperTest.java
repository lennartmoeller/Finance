package com.lennartmoeller.finance.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.lennartmoeller.finance.dto.AccountDTO;
import com.lennartmoeller.finance.model.Account;
import org.junit.jupiter.api.Test;

class AccountMapperTest {

    private final AccountMapper mapper = new AccountMapperImpl();

    @Test
    void testToDtoAndBack() {
        Account account = new Account();
        account.setId(1L);
        account.setLabel("Checking");
        account.setStartBalance(500L);
        account.setActive(false);
        account.setDeposits(true);

        AccountDTO dto = mapper.toDto(account);
        assertNotNull(dto);
        assertEquals(account.getId(), dto.getId());
        assertEquals(account.getLabel(), dto.getLabel());
        assertEquals(account.getStartBalance(), dto.getStartBalance());
        assertEquals(account.getActive(), dto.getActive());

        Account entity = mapper.toEntity(dto);
        assertNotNull(entity);
        assertEquals(account.getId(), entity.getId());
        assertEquals(account.getLabel(), entity.getLabel());
        assertEquals(account.getStartBalance(), entity.getStartBalance());
        assertEquals(account.getActive(), entity.getActive());
        // deposits is not part of the DTO and should remain default (false)
        assertFalse(entity.getDeposits());
    }

    @Test
    void testNullValues() {
        assertNull(mapper.toDto(null));
        assertNull(mapper.toEntity(null));
    }
}
