package com.lennartmoeller.finance.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.TransactionDTO;
import com.lennartmoeller.finance.service.TransactionService;
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
class TransactionControllerTest {
    @Mock
    private TransactionService service;

    @InjectMocks
    private TransactionController controller;

    @Test
    void shouldReturnAllTransactions() {
        List<TransactionDTO> list = List.of(new TransactionDTO());
        when(service.findAll()).thenReturn(list);

        List<TransactionDTO> result = controller.getTransactions();

        assertThat(result).isEqualTo(list);
        verify(service).findAll();
    }

    @Test
    void shouldReturnTransactionById() {
        TransactionDTO dto = new TransactionDTO();
        when(service.findById(1L)).thenReturn(Optional.of(dto));

        ResponseEntity<TransactionDTO> response = controller.getTransactionById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(dto);
    }

    @Test
    void shouldReturnNotFoundForUnknownTransactionId() {
        when(service.findById(2L)).thenReturn(Optional.empty());

        ResponseEntity<TransactionDTO> response = controller.getTransactionById(2L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void shouldUpdateExistingTransaction() {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(5L);
        TransactionDTO saved = new TransactionDTO();
        when(service.findById(5L)).thenReturn(Optional.of(new TransactionDTO()));
        when(service.save(dto)).thenReturn(saved);

        TransactionDTO result = controller.createOrUpdateTransaction(dto);

        assertThat(result).isSameAs(saved);
        assertThat(dto.getId()).isEqualTo(5L);
        verify(service).save(dto);
    }

    @Test
    void shouldCreateNewTransactionWhenIdUnknown() {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(5L);
        TransactionDTO saved = new TransactionDTO();
        when(service.findById(5L)).thenReturn(Optional.empty());
        when(service.save(any())).thenReturn(saved);

        TransactionDTO result = controller.createOrUpdateTransaction(dto);

        assertThat(result).isSameAs(saved);
        ArgumentCaptor<TransactionDTO> captor = ArgumentCaptor.forClass(TransactionDTO.class);
        verify(service).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
    }

    @Test
    void shouldCreateTransactionWhenIdIsNull() {
        TransactionDTO dto = new TransactionDTO();
        when(service.save(dto)).thenReturn(dto);

        TransactionDTO result = controller.createOrUpdateTransaction(dto);

        assertThat(result).isSameAs(dto);
        verify(service).save(dto);
        verify(service, never()).findById(any());
    }

    @Test
    void shouldDeleteTransaction() {
        ResponseEntity<Void> response = controller.deleteTransaction(9L);

        verify(service).deleteById(9L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }
}
