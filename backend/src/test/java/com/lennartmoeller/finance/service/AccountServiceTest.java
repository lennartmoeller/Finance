package com.lennartmoeller.finance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.AccountDTO;
import com.lennartmoeller.finance.mapper.AccountMapper;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.repository.AccountRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccountServiceTest {
    private AccountRepository accountRepository;
    private AccountMapper accountMapper;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        accountMapper = mock(AccountMapper.class);
        accountService = new AccountService(accountRepository, accountMapper);
    }

    @Test
    void testFindAllSorted() {
        Account a1 = new Account();
        a1.setId(1L);
        a1.setLabel("B");
        Account a2 = new Account();
        a2.setId(2L);
        a2.setLabel("A");
        when(accountRepository.findAll()).thenReturn(List.of(a1, a2));

        AccountDTO d1 = new AccountDTO();
        d1.setLabel("B");
        AccountDTO d2 = new AccountDTO();
        d2.setLabel("A");
        when(accountMapper.toDto(a1)).thenReturn(d1);
        when(accountMapper.toDto(a2)).thenReturn(d2);

        List<AccountDTO> result = accountService.findAll();

        assertEquals(List.of(d2, d1), result); // sorted by label
    }

    @Test
    void testFindByIdFound() {
        Account account = new Account();
        account.setId(5L);
        AccountDTO dto = new AccountDTO();
        when(accountRepository.findById(5L)).thenReturn(Optional.of(account));
        when(accountMapper.toDto(account)).thenReturn(dto);

        Optional<AccountDTO> result = accountService.findById(5L);

        assertTrue(result.isPresent());
        assertEquals(dto, result.get());
    }

    @Test
    void testFindByIdNotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<AccountDTO> result = accountService.findById(99L);

        assertTrue(result.isEmpty());
        verifyNoInteractions(accountMapper);
    }

    @Test
    void testSave() {
        AccountDTO dtoIn = new AccountDTO();
        Account entity = new Account();
        Account saved = new Account();
        AccountDTO dtoOut = new AccountDTO();

        when(accountMapper.toEntity(dtoIn)).thenReturn(entity);
        when(accountRepository.save(entity)).thenReturn(saved);
        when(accountMapper.toDto(saved)).thenReturn(dtoOut);

        AccountDTO result = accountService.save(dtoIn);

        assertEquals(dtoOut, result);
    }

    @Test
    void testDeleteById() {
        accountService.deleteById(42L);
        verify(accountRepository).deleteById(42L);
    }
}
