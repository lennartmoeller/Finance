package com.lennartmoeller.finance.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.lennartmoeller.finance.csv.CamtV8CsvParser;
import com.lennartmoeller.finance.csv.IngV1CsvParser;
import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import com.lennartmoeller.finance.mapper.BankTransactionMapper;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.BankType;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

class BankCsvImportServiceTest {

    private BankTransactionRepository repository;
    private BankTransactionMapper mapper;
    private IngV1CsvParser ingParser;
    private CamtV8CsvParser camtParser;
    private BankCsvImportService service;

    @BeforeEach
    void setUp() {
        repository = mock(BankTransactionRepository.class);
        mapper = mock(BankTransactionMapper.class);
        ingParser = mock(IngV1CsvParser.class);
        camtParser = mock(CamtV8CsvParser.class);
        service = new BankCsvImportService(repository, mapper, ingParser, camtParser);
    }

    @Test
    void testImportCsvFiltersDuplicates() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        IngV1TransactionDTO dto = new IngV1TransactionDTO();
        dto.setIban("DE");
        dto.setBookingDate(java.time.LocalDate.now());
        dto.setPurpose("p");
        dto.setCounterparty("c");
        dto.setAmount(1L);
        when(file.getInputStream()).thenReturn(InputStream.nullInputStream());
        when(ingParser.parse(any())).thenReturn(List.of(dto));
        when(repository.existsDuplicate(any(), any(), any(), any(), any())).thenReturn(false);
        BankTransaction entity = new BankTransaction();
        when(mapper.toEntity(dto)).thenReturn(entity);
        BankTransaction saved = new BankTransaction();
        when(repository.save(entity)).thenReturn(saved);
        BankTransactionDTO resultDto = new BankTransactionDTO();
        when(mapper.toDto(saved)).thenReturn(resultDto);

        List<BankTransactionDTO> result = service.importCsv(BankType.ING_V1, file);

        assertEquals(List.of(resultDto), result);
        verify(repository).save(entity);
    }
}
