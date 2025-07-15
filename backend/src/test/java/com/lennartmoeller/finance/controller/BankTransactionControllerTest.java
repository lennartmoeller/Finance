package com.lennartmoeller.finance.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.BankCsvImportStatsDTO;
import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import com.lennartmoeller.finance.service.BankCsvImportService;
import com.lennartmoeller.finance.service.BankTransactionService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        BankCsvImportStatsDTO stats = new BankCsvImportStatsDTO();
        stats.setImports(1);
        when(importService.importCsv(file)).thenReturn(stats);

        BankCsvImportStatsDTO result = controller.importCsv(file);

        assertThat(result.getImports()).isEqualTo(1);
        verify(importService).importCsv(file);
    }

    @Test
    void shouldPropagateIOExceptionDuringImport() throws IOException {
        when(importService.importCsv(file)).thenThrow(new IOException("fail"));

        assertThatThrownBy(() -> controller.importCsv(file)).isInstanceOf(IOException.class);
        verify(importService).importCsv(file);
    }

    @Test
    void shouldReturnTransactions() {
        List<BankTransactionDTO> list = List.of(new IngV1TransactionDTO());
        when(service.findAll()).thenReturn(list);

        List<BankTransactionDTO> result = controller.getBankTransactions();

        assertThat(result).isEqualTo(list);
        verify(service).findAll();
    }

    @Test
    void shouldReturnTransactionWhenIdExists() {
        BankTransactionDTO dto = new IngV1TransactionDTO();
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
}
