package com.lennartmoeller.finance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.converter.MapToJsonStringConverter;
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
    private TransactionLinkSuggestionService suggestionService;
    private BankCsvImportService service;

    @BeforeEach
    void setUp() {
        repository = mock(BankTransactionRepository.class);
        mapper = mock(BankTransactionMapper.class);
        ingParser = mock(IngV1CsvParser.class);
        camtParser = mock(CamtV8CsvParser.class);
        MapToJsonStringConverter converter = new MapToJsonStringConverter();
        accountRepository = mock(AccountRepository.class);
        suggestionService = mock(TransactionLinkSuggestionService.class);
        service = new BankCsvImportService(
                accountRepository, mapper, repository, camtParser, ingParser, converter, suggestionService);
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
        when(mapper.toEntity((BankTransactionDTO) dto, account)).thenReturn(entity);
        when(repository.findAllDatas()).thenReturn(List.of());
        BankTransaction saved = new BankTransaction();
        when(repository.saveAll(List.of(entity))).thenReturn(List.of(saved));
        BankTransactionDTO resultDto = new IngV1TransactionDTO();
        when(mapper.toDto(saved)).thenReturn(resultDto);

        BankTransactionImportResultDTO result = service.importCsv(BankType.ING_V1, file);

        assertEquals(List.of(resultDto), result.getSaved());
        assertTrue(result.getUnsaved().isEmpty());
        verify(repository).saveAll(List.of(entity));
        verify(suggestionService).updateAllFor(List.of(saved), null);
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
        when(mapper.toEntity((BankTransactionDTO) dto, account)).thenReturn(entity);
        when(repository.findAllDatas()).thenReturn(List.of(entity.getData()));

        BankTransactionImportResultDTO result = service.importCsv(BankType.ING_V1, file);

        assertTrue(result.getSaved().isEmpty());
        assertEquals(List.of(dto), result.getUnsaved());
        verify(repository, never()).saveAll(any());
        verify(suggestionService).updateAllFor(List.of(), null);
    }

    @Test
    void testImportCsvCamt() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        CamtV8TransactionDTO dto = new CamtV8TransactionDTO();
        when(file.getInputStream()).thenReturn(InputStream.nullInputStream());
        when(camtParser.parse(any())).thenReturn(List.of(dto));
        when(accountRepository.findAllByIbanIn(java.util.Collections.emptySet()))
                .thenReturn(java.util.Collections.emptyList());
        when(mapper.toEntity(eq((BankTransactionDTO) dto), isNull())).thenReturn(new BankTransaction());

        BankTransactionImportResultDTO result = service.importCsv(BankType.CAMT_V8, file);

        assertTrue(result.getSaved().isEmpty());
        assertEquals(List.of(dto), result.getUnsaved());
        verify(repository, never()).saveAll(any());
        verify(suggestionService).updateAllFor(List.of(), null);
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
        when(ingParser.parse(any())).thenReturn(List.of(dto1, dto2));
        Account account = new Account();
        account.setIban("DE");
        when(accountRepository.findAllByIbanIn(java.util.Set.of("DE"))).thenReturn(List.of(account));
        BankTransaction e1 = new BankTransaction();
        e1.setAccount(account);
        BankTransaction e2 = new BankTransaction();
        e2.setAccount(account);
        when(mapper.toEntity((BankTransactionDTO) dto1, account)).thenReturn(e1);
        when(mapper.toEntity((BankTransactionDTO) dto2, account)).thenReturn(e2);
        when(repository.findAllDatas()).thenReturn(List.of());
        when(repository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toDto(same(e1))).thenReturn(dto1);
        when(mapper.toDto(same(e2))).thenReturn(dto2);

        BankTransactionImportResultDTO result = service.importCsv(BankType.ING_V1, file);

        assertEquals(List.of(dto2, dto1), result.getSaved());
        assertTrue(result.getUnsaved().isEmpty());
        verify(suggestionService).updateAllFor(List.of(e2, e1), null);
    }

    @Test
    void testImportCsvHandlesNullIban() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        IngV1TransactionDTO dto = new IngV1TransactionDTO();
        dto.setIban(null);
        dto.setBookingDate(java.time.LocalDate.now());
        dto.setPurpose("p");
        dto.setCounterparty("c");
        dto.setAmount(1L);

        when(file.getInputStream()).thenReturn(InputStream.nullInputStream());
        when(ingParser.parse(any())).thenReturn(List.of(dto));
        when(accountRepository.findAllByIbanIn(java.util.Collections.emptySet()))
                .thenReturn(java.util.Collections.emptyList());
        BankTransaction entity = new BankTransaction();
        when(mapper.toEntity((BankTransactionDTO) dto, null)).thenReturn(entity);
        when(repository.findAllDatas()).thenReturn(List.of());
        when(repository.saveAll(any())).thenReturn(List.of());

        BankTransactionImportResultDTO result = service.importCsv(BankType.ING_V1, file);

        assertTrue(result.getSaved().isEmpty());
        assertEquals(List.of(dto), result.getUnsaved());
        verify(suggestionService).updateAllFor(List.of(), null);
    }
}
