package com.lennartmoeller.finance.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.model.BankType;
import com.lennartmoeller.finance.service.BankCsvImportService;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

class BankTransactionControllerTest {

    private BankCsvImportService service;
    private BankTransactionController controller;

    @BeforeEach
    void setUp() {
        service = mock(BankCsvImportService.class);
        controller = new BankTransactionController(service);
    }

    @Test
    void testImportCsv() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        BankTransactionDTO dto = new BankTransactionDTO();
        when(service.importCsv(BankType.ING_V1, file)).thenReturn(List.of(dto));

        List<BankTransactionDTO> result = controller.importCsv(BankType.ING_V1, file);

        assertEquals(List.of(dto), result);
        verify(service).importCsv(BankType.ING_V1, file);
    }
}
