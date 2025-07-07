package com.lennartmoeller.finance.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.BankTransactionImportResultDTO;
import com.lennartmoeller.finance.model.BankType;
import com.lennartmoeller.finance.service.BankCsvImportService;
import com.lennartmoeller.finance.service.BankTransactionService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

class BankTransactionControllerTest {
    private BankCsvImportService importService;
    private BankTransactionService service;
    private BankTransactionController controller;

    @BeforeEach
    void setUp() {
        importService = mock(BankCsvImportService.class);
        service = mock(BankTransactionService.class);
        controller = new BankTransactionController(importService, service);
    }

    @Test
    void testImportCsv() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        BankTransactionDTO dto = new BankTransactionDTO();
        BankTransactionImportResultDTO resultDto = new BankTransactionImportResultDTO(List.of(dto), List.of());
        when(importService.importCsv(BankType.ING_V1, file)).thenReturn(resultDto);

        BankTransactionImportResultDTO result = controller.importCsv(BankType.ING_V1, file);

        assertEquals(List.of(dto), result.getSaved());
        assertTrue(result.getUnsaved().isEmpty());
        verify(importService).importCsv(BankType.ING_V1, file);
    }

    @Test
    void testGetBankTransactions() {
        List<BankTransactionDTO> list = List.of(new BankTransactionDTO());
        when(service.findAll()).thenReturn(list);

        List<BankTransactionDTO> result = controller.getBankTransactions();

        assertEquals(list, result);
        verify(service).findAll();
    }

    @Test
    void testGetBankTransactionByIdFound() {
        BankTransactionDTO dto = new BankTransactionDTO();
        when(service.findById(1L)).thenReturn(Optional.of(dto));

        ResponseEntity<BankTransactionDTO> response = controller.getBankTransactionById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testGetBankTransactionByIdNotFound() {
        when(service.findById(2L)).thenReturn(Optional.empty());

        ResponseEntity<BankTransactionDTO> response = controller.getBankTransactionById(2L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testCreateOrUpdateBankTransactionExisting() {
        BankTransactionDTO dto = new BankTransactionDTO();
        dto.setId(3L);
        BankTransactionDTO saved = new BankTransactionDTO();

        when(service.findById(3L)).thenReturn(Optional.of(new BankTransactionDTO()));
        when(service.save(dto)).thenReturn(saved);

        BankTransactionDTO result = controller.createOrUpdateBankTransaction(dto);

        assertEquals(saved, result);
        assertEquals(3L, dto.getId());
        verify(service).save(dto);
    }

    @Test
    void testCreateOrUpdateBankTransactionNew() {
        BankTransactionDTO dto = new BankTransactionDTO();
        dto.setId(4L);
        BankTransactionDTO saved = new BankTransactionDTO();

        when(service.findById(4L)).thenReturn(Optional.empty());
        when(service.save(any())).thenReturn(saved);

        BankTransactionDTO result = controller.createOrUpdateBankTransaction(dto);

        assertEquals(saved, result);
        ArgumentCaptor<BankTransactionDTO> captor = ArgumentCaptor.forClass(BankTransactionDTO.class);
        verify(service).save(captor.capture());
        assertNull(captor.getValue().getId());
    }

    @Test
    void testDeleteBankTransaction() {
        ResponseEntity<Void> response = controller.deleteBankTransaction(5L);

        verify(service).deleteById(5L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }
}
