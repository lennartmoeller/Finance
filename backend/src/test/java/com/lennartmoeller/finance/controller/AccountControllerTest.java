package com.lennartmoeller.finance.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.AccountDTO;
import com.lennartmoeller.finance.service.AccountService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class AccountControllerTest {
    private AccountService service;
    private AccountController controller;

    @BeforeEach
    void setUp() {
        service = mock(AccountService.class);
        controller = new AccountController(service);
    }

    @Test
    void testGetAccounts() {
        List<AccountDTO> list = List.of(new AccountDTO(), new AccountDTO());
        when(service.findAll()).thenReturn(list);

        List<AccountDTO> result = controller.getAccounts();

        assertEquals(list, result);
        verify(service).findAll();
    }

    @Test
    void testGetAccountByIdFound() {
        AccountDTO dto = new AccountDTO();
        when(service.findById(1L)).thenReturn(Optional.of(dto));

        ResponseEntity<AccountDTO> response = controller.getAccountById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testGetAccountByIdNotFound() {
        when(service.findById(2L)).thenReturn(Optional.empty());

        ResponseEntity<AccountDTO> response = controller.getAccountById(2L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testCreateOrUpdateAccountExisting() {
        AccountDTO dto = new AccountDTO();
        dto.setId(5L);
        AccountDTO saved = new AccountDTO();

        when(service.findById(5L)).thenReturn(Optional.of(new AccountDTO()));
        when(service.save(dto)).thenReturn(saved);

        AccountDTO result = controller.createOrUpdateAccount(dto);

        assertEquals(saved, result);
        assertEquals(5L, dto.getId());
        verify(service).save(dto);
    }

    @Test
    void testCreateOrUpdateAccountNew() {
        AccountDTO dto = new AccountDTO();
        dto.setId(5L);
        AccountDTO saved = new AccountDTO();

        when(service.findById(5L)).thenReturn(Optional.empty());
        when(service.save(any())).thenReturn(saved);

        AccountDTO result = controller.createOrUpdateAccount(dto);

        assertEquals(saved, result);
        ArgumentCaptor<AccountDTO> captor = ArgumentCaptor.forClass(AccountDTO.class);
        verify(service).save(captor.capture());
        assertNull(captor.getValue().getId());
    }

    @Test
    void testDeleteAccount() {
        ResponseEntity<Void> response = controller.deleteAccount(9L);

        verify(service).deleteById(9L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }
}
