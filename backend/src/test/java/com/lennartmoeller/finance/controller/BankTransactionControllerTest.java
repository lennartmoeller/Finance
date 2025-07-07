package com.lennartmoeller.finance.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.BankTransactionImportResultDTO;
import com.lennartmoeller.finance.model.BankType;
import com.lennartmoeller.finance.service.BankCsvImportService;
import com.lennartmoeller.finance.service.BankTransactionService;
import java.io.IOException;
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
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class BankTransactionControllerTest {
    @Mock
    private BankCsvImportService importService;

    @Mock
    private BankTransactionService service;

    @InjectMocks
    private BankTransactionController controller;

    @Mock
    private MultipartFile file;

    @Test
    void shouldImportCsv() throws IOException {
        BankTransactionDTO dto = new BankTransactionDTO();
        BankTransactionImportResultDTO resultDto = new BankTransactionImportResultDTO(List.of(dto), List.of());
        when(importService.importCsv(BankType.ING_V1, file)).thenReturn(resultDto);

        BankTransactionImportResultDTO result = controller.importCsv(BankType.ING_V1, file);

        assertThat(result.getSaved()).containsExactly(dto);
        assertThat(result.getUnsaved()).isEmpty();
        verify(importService).importCsv(BankType.ING_V1, file);
    }

    @Test
    void shouldPropagateIOExceptionDuringImport() throws IOException {
        when(importService.importCsv(BankType.ING_V1, file)).thenThrow(new IOException("fail"));

        assertThatThrownBy(() -> controller.importCsv(BankType.ING_V1, file)).isInstanceOf(IOException.class);
        verify(importService).importCsv(BankType.ING_V1, file);
    }

    @Test
    void shouldReturnTransactions() {
        List<BankTransactionDTO> list = List.of(new BankTransactionDTO());
        when(service.findAll()).thenReturn(list);

        List<BankTransactionDTO> result = controller.getBankTransactions();

        assertThat(result).isEqualTo(list);
        verify(service).findAll();
    }

    @Test
    void shouldReturnTransactionWhenIdExists() {
        BankTransactionDTO dto = new BankTransactionDTO();
        when(service.findById(1L)).thenReturn(Optional.of(dto));

        ResponseEntity<BankTransactionDTO> response = controller.getBankTransactionById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(dto);
    }

    @Test
    void shouldReturnNotFoundForUnknownId() {
        when(service.findById(2L)).thenReturn(Optional.empty());

        ResponseEntity<BankTransactionDTO> response = controller.getBankTransactionById(2L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void shouldUpdateExistingTransaction() {
        BankTransactionDTO dto = new BankTransactionDTO();
        dto.setId(3L);
        BankTransactionDTO saved = new BankTransactionDTO();
        when(service.findById(3L)).thenReturn(Optional.of(new BankTransactionDTO()));
        when(service.save(dto)).thenReturn(saved);

        BankTransactionDTO result = controller.createOrUpdateBankTransaction(dto);

        assertThat(result).isSameAs(saved);
        assertThat(dto.getId()).isEqualTo(3L);
        verify(service).save(dto);
    }

    @Test
    void shouldCreateNewTransactionWhenIdUnknown() {
        BankTransactionDTO dto = new BankTransactionDTO();
        dto.setId(4L);
        BankTransactionDTO saved = new BankTransactionDTO();
        when(service.findById(4L)).thenReturn(Optional.empty());
        when(service.save(any())).thenReturn(saved);

        BankTransactionDTO result = controller.createOrUpdateBankTransaction(dto);

        assertThat(result).isSameAs(saved);
        ArgumentCaptor<BankTransactionDTO> captor = ArgumentCaptor.forClass(BankTransactionDTO.class);
        verify(service).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
    }

    @Test
    void shouldCreateTransactionWhenIdIsNull() {
        BankTransactionDTO dto = new BankTransactionDTO();
        when(service.save(dto)).thenReturn(dto);

        BankTransactionDTO result = controller.createOrUpdateBankTransaction(dto);

        assertThat(result).isSameAs(dto);
        verify(service).save(dto);
        verify(service, never()).findById(any());
    }

    @Test
    void shouldDeleteTransaction() {
        ResponseEntity<Void> response = controller.deleteBankTransaction(5L);

        verify(service).deleteById(5L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }
}
