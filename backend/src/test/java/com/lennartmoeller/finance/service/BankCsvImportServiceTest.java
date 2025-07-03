package com.lennartmoeller.finance.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.lennartmoeller.finance.csv.CamtV8CsvParser;
import com.lennartmoeller.finance.csv.IngV1CsvParser;
import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.BankTransactionImportResultDTO;
import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import com.lennartmoeller.finance.mapper.BankTransactionMapper;
import com.lennartmoeller.finance.model.Account;
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
    private BankTransactionMapper mapper;
    private IngV1CsvParser ingParser;
    private CamtV8CsvParser camtParser;
    private AccountRepository accountRepository;
    private BankCsvImportService service;

    @BeforeEach
    void setUp() {
        repository = mock(BankTransactionRepository.class);
        mapper = mock(BankTransactionMapper.class);
        ingParser = mock(IngV1CsvParser.class);
        camtParser = mock(CamtV8CsvParser.class);
        accountRepository = mock(AccountRepository.class);
        service = new BankCsvImportService(repository, mapper, ingParser, camtParser, accountRepository);
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
        Account account = new Account();
        account.setIban("DE");
        when(accountRepository.findAllByIbanIn(java.util.Set.of("DE"))).thenReturn(List.of(account));
        BankTransaction entity = new BankTransaction();
        entity.setAccount(account);
        when(mapper.toEntity(eq(dto), eq(account))).thenReturn(entity);
        when(repository.existsByAccountAndBookingDateAndPurposeAndCounterpartyAndAmount(
                        eq(account), any(), any(), any(), any()))
                .thenReturn(false);
        BankTransaction saved = new BankTransaction();
        when(repository.save(entity)).thenReturn(saved);
        BankTransactionDTO resultDto = new BankTransactionDTO();
        when(mapper.toDto(saved)).thenReturn(resultDto);

        BankTransactionImportResultDTO result = service.importCsv(BankType.ING_V1, file);

        assertEquals(List.of(resultDto), result.getSaved());
        assertTrue(result.getUnsaved().isEmpty());
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
        Account account = new Account();
        account.setIban("DE");
        when(accountRepository.findAllByIbanIn(java.util.Set.of("DE"))).thenReturn(List.of(account));
        BankTransaction entity = new BankTransaction();
        entity.setAccount(account);
        when(mapper.toEntity(eq(dto), eq(account))).thenReturn(entity);
        when(repository.existsByAccountAndBookingDateAndPurposeAndCounterpartyAndAmount(
                        eq(account), any(), any(), any(), any()))
                .thenReturn(true);

        BankTransactionImportResultDTO result = service.importCsv(BankType.ING_V1, file);

        assertTrue(result.getSaved().isEmpty());
        assertEquals(List.of(dto), result.getUnsaved());
        verify(repository, never()).save(any());
    }

    @Test
    void testImportCsvCamt() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        CamtV8TransactionDTO dto = new CamtV8TransactionDTO();
        when(file.getInputStream()).thenReturn(InputStream.nullInputStream());
        when(camtParser.parse(any())).thenReturn(List.of(dto));
        when(accountRepository.findAllByIbanIn(java.util.Collections.emptySet()))
                .thenReturn(java.util.Collections.emptyList());
        when(mapper.toEntity(eq(dto), isNull())).thenReturn(new BankTransaction());

        BankTransactionImportResultDTO result = service.importCsv(BankType.CAMT_V8, file);

        assertTrue(result.getSaved().isEmpty());
        assertEquals(List.of(dto), result.getUnsaved());
        verify(repository, never()).save(any());
    }

    @Test
    void testImportCsvDefaultAndSorting() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        BankTransactionDTO dto1 = new BankTransactionDTO();
        dto1.setIban("DE");
        dto1.setBookingDate(java.time.LocalDate.of(2024, 2, 2));
        dto1.setPurpose("p1");
        dto1.setCounterparty("c");
        dto1.setAmount(1L);

        BankTransactionDTO dto2 = new BankTransactionDTO();
        dto2.setIban("DE");
        dto2.setBookingDate(java.time.LocalDate.of(2024, 1, 1));
        dto2.setPurpose("p2");
        dto2.setCounterparty("c");
        dto2.setAmount(2L);

        when(file.getInputStream()).thenReturn(InputStream.nullInputStream());
        when(ingParser.parse(any())).thenReturn((List) List.of(dto1, dto2));
        Account account = new Account();
        account.setIban("DE");
        when(accountRepository.findAllByIbanIn(java.util.Set.of("DE"))).thenReturn(List.of(account));
        BankTransaction e1 = new BankTransaction();
        e1.setAccount(account);
        BankTransaction e2 = new BankTransaction();
        e2.setAccount(account);
        when(mapper.toEntity(eq(dto1), eq(account))).thenReturn(e1);
        when(mapper.toEntity(eq(dto2), eq(account))).thenReturn(e2);
        when(repository.existsByAccountAndBookingDateAndPurposeAndCounterpartyAndAmount(
                        eq(account), any(), any(), any(), any()))
                .thenReturn(false);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toDto(same(e1))).thenReturn(dto1);
        when(mapper.toDto(same(e2))).thenReturn(dto2);

        BankTransactionImportResultDTO result = service.importCsv(BankType.ING_V1, file);

        assertEquals(List.of(dto2, dto1), result.getSaved());
        assertTrue(result.getUnsaved().isEmpty());
    }
}
