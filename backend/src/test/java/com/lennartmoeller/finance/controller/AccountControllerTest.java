package com.lennartmoeller.finance.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.AccountDTO;
import com.lennartmoeller.finance.service.AccountService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {
    @Mock
    private AccountService service;

    @InjectMocks
    private AccountController controller;

    @Test
    void shouldReturnAllAccounts() {
        List<AccountDTO> list = List.of(new AccountDTO(), new AccountDTO());
        when(service.findAll()).thenReturn(list);

        List<AccountDTO> result = controller.getAccounts();

        assertThat(result).isEqualTo(list);
        verify(service).findAll();
    }

    @Test
    void shouldReturnAccountWhenIdExists() {
        AccountDTO dto = new AccountDTO();
        when(service.findById(1L)).thenReturn(Optional.of(dto));

        ResponseEntity<AccountDTO> response = controller.getAccountById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(dto);
    }

    @Test
    void shouldReturnNotFoundWhenIdMissing() {
        when(service.findById(2L)).thenReturn(Optional.empty());

        ResponseEntity<AccountDTO> response = controller.getAccountById(2L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void shouldUpdateAccountWhenExisting() {
        AccountDTO dto = new AccountDTO();
        dto.setId(5L);
        AccountDTO saved = new AccountDTO();
        when(service.findById(5L)).thenReturn(Optional.of(new AccountDTO()));
        when(service.save(dto)).thenReturn(saved);

        AccountDTO result = controller.createOrUpdateAccount(dto);

        assertThat(result).isSameAs(saved);
        assertThat(dto.getId()).isEqualTo(5L);
        verify(service).save(dto);
    }

    @Test
    void shouldCreateNewAccountWhenIdUnknown() {
        AccountDTO dto = new AccountDTO();
        dto.setId(5L);
        AccountDTO saved = new AccountDTO();
        when(service.findById(5L)).thenReturn(Optional.empty());
        when(service.save(any())).thenReturn(saved);

        AccountDTO result = controller.createOrUpdateAccount(dto);

        assertThat(result).isSameAs(saved);
        ArgumentCaptor<AccountDTO> captor = ArgumentCaptor.forClass(AccountDTO.class);
        verify(service).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
    }

    @Test
    void shouldCreateAccountWhenIdIsNull() {
        AccountDTO dto = new AccountDTO();
        when(service.save(dto)).thenReturn(dto);

        AccountDTO result = controller.createOrUpdateAccount(dto);

        assertThat(result).isSameAs(dto);
        verify(service).save(dto);
        verify(service, never()).findById(any());
    }

    @Test
    void shouldDeleteAccount() {
        ResponseEntity<Void> response = controller.deleteAccount(9L);

        verify(service).deleteById(9L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }
}
