package com.lennartmoeller.finance.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.lennartmoeller.finance.csv.CamtV8CsvParser;
import com.lennartmoeller.finance.csv.IngV1CsvParser;
import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import com.lennartmoeller.finance.mapper.BankTransactionMapper;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.BankType;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

class BankCsvImportServiceTest {

    private BankTransactionRepository repository;
    private AccountRepository accountRepository;
    private BankTransactionMapper mapper;
    private IngV1CsvParser ingParser;
    private CamtV8CsvParser camtParser;
    private BankCsvImportService service;

    @BeforeEach
    void setUp() {
        repository = mock(BankTransactionRepository.class);
        accountRepository = mock(AccountRepository.class);
        mapper = mock(BankTransactionMapper.class);
        ingParser = mock(IngV1CsvParser.class);
        camtParser = mock(CamtV8CsvParser.class);
        service = new BankCsvImportService(repository, accountRepository, mapper, ingParser, camtParser);
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
        com.lennartmoeller.finance.model.Account acc = new com.lennartmoeller.finance.model.Account();
        acc.setId(1L);
        when(accountRepository.findByIban("DE")).thenReturn(java.util.Optional.of(acc));
        when(repository.existsByAccountAndBookingDateAndPurposeAndCounterpartyAndAmount(
                        eq(acc), any(), any(), any(), any()))
                .thenReturn(false);
        BankTransaction entity = new BankTransaction();
        when(mapper.toEntity(eq(dto), same(accountRepository))).thenReturn(entity);
        BankTransaction saved = new BankTransaction();
        when(repository.save(entity)).thenReturn(saved);
        BankTransactionDTO resultDto = new BankTransactionDTO();
        when(mapper.toDto(saved)).thenReturn(resultDto);

        List<BankTransactionDTO> result = service.importCsv(BankType.ING_V1, file);

        assertEquals(List.of(resultDto), result);
        verify(repository).save(entity);
    }

    @Test
    void testImportCsvSkipsExisting() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        IngV1TransactionDTO dto = new IngV1TransactionDTO();
        dto.setIban("DE");
        dto.setBookingDate(java.time.LocalDate.now());
        dto.setPurpose("p");
        dto.setCounterparty("c");
        dto.setAmount(1L);
        when(file.getInputStream()).thenReturn(InputStream.nullInputStream());
        when(ingParser.parse(any())).thenReturn(List.of(dto));
        com.lennartmoeller.finance.model.Account acc = new com.lennartmoeller.finance.model.Account();
        acc.setId(2L);
        when(accountRepository.findByIban("DE")).thenReturn(java.util.Optional.of(acc));
        when(repository.existsByAccountAndBookingDateAndPurposeAndCounterpartyAndAmount(
                        eq(acc), any(), any(), any(), any()))
                .thenReturn(true);

        List<BankTransactionDTO> result = service.importCsv(BankType.ING_V1, file);

        assertTrue(result.isEmpty());
        verify(repository, never()).save(any());
    }

    @Test
    void testImportCsvCamt() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        CamtV8TransactionDTO dto = new CamtV8TransactionDTO();
        dto.setIban("DE");
        when(file.getInputStream()).thenReturn(InputStream.nullInputStream());
        when(camtParser.parse(any())).thenReturn(List.of(dto));
        com.lennartmoeller.finance.model.Account acc = new com.lennartmoeller.finance.model.Account();
        acc.setId(3L);
        when(accountRepository.findByIban("DE")).thenReturn(java.util.Optional.of(acc));
        when(repository.existsByAccountAndBookingDateAndPurposeAndCounterpartyAndAmount(
                        eq(acc), any(), any(), any(), any()))
                .thenReturn(false);
        BankTransaction entity = new BankTransaction();
        when(mapper.toEntity(eq(dto), same(accountRepository))).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        List<BankTransactionDTO> result = service.importCsv(BankType.CAMT_V8, file);

        assertEquals(List.of(dto), result);
    }

    @Test
    void testImportCsvDefaultAndSorting() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        IngV1TransactionDTO dto1 = new IngV1TransactionDTO();
        dto1.setIban("DE");
        dto1.setBookingDate(java.time.LocalDate.of(2024, 2, 2));
        dto1.setPurpose("p1");
        dto1.setCounterparty("c");
        dto1.setAmount(1L);

        IngV1TransactionDTO dto2 = new IngV1TransactionDTO();
        dto2.setIban("DE");
        dto2.setBookingDate(java.time.LocalDate.of(2024, 1, 1));
        dto2.setPurpose("p2");
        dto2.setCounterparty("c");
        dto2.setAmount(2L);

        when(file.getInputStream()).thenReturn(InputStream.nullInputStream());
        when(ingParser.parse(any())).thenReturn((List) List.of(dto1, dto2));
        com.lennartmoeller.finance.model.Account acc = new com.lennartmoeller.finance.model.Account();
        acc.setId(5L);
        when(accountRepository.findByIban("DE")).thenReturn(java.util.Optional.of(acc));
        when(repository.existsByAccountAndBookingDateAndPurposeAndCounterpartyAndAmount(
                        eq(acc), any(), any(), any(), any()))
                .thenReturn(false);

        BankTransaction e1 = new BankTransaction();
        BankTransaction e2 = new BankTransaction();
        when(mapper.toEntity(eq(dto1), same(accountRepository))).thenReturn(e1);
        when(mapper.toEntity(eq(dto2), same(accountRepository))).thenReturn(e2);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toDto(same(e1))).thenReturn(dto1);
        when(mapper.toDto(same(e2))).thenReturn(dto2);

        List<BankTransactionDTO> result = service.importCsv(BankType.ING_V1, file);

        assertEquals(List.of(dto2, dto1), result);
    }
}
