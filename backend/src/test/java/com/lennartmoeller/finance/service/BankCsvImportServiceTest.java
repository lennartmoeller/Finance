package com.lennartmoeller.finance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.csv.BankCsvParser;
import com.lennartmoeller.finance.dto.BankCsvImportStatsDTO;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

class BankCsvImportServiceTest {
    private BankTransactionRepository repository;
    private AccountRepository accountRepository;
    private TransactionLinkSuggestionService suggestionService;
    private BankCsvImportService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(BankTransactionRepository.class);
        accountRepository = Mockito.mock(AccountRepository.class);
        suggestionService = Mockito.mock(TransactionLinkSuggestionService.class);
        service = new BankCsvImportService(accountRepository, repository, suggestionService);
    }

    @Test
    void savesNewTransactionsAndCountsDuplicates() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Account account = new Account();
        account.setId(1L);
        account.setIban("DE");
        when(accountRepository.findByIbanIsNotNull()).thenReturn(List.of(account));
        BankTransaction t1 = new BankTransaction();
        t1.setAccount(account);
        t1.setBookingDate(java.time.LocalDate.now());
        t1.setPurpose("p");
        t1.setCounterparty("c");
        t1.setAmount(1L);
        t1.setData("d1");
        try (MockedStatic<BankCsvParser> mock = Mockito.mockStatic(BankCsvParser.class)) {
            mock.when(() -> BankCsvParser.parse(file, Map.of("DE", account))).thenReturn(List.of(t1));
            when(repository.findAll()).thenReturn(List.of(t1));
            when(repository.saveAll(List.of())).thenReturn(List.of());

            BankCsvImportStatsDTO stats = service.importCsv(file);

            assertEquals(0, stats.getImports());
            assertEquals(1, stats.getDuplicates());
            assertEquals(0, stats.getErrors());
            verify(repository).saveAll(List.of());
            verify(suggestionService).updateAllFor(List.of(), null);
        }
    }

    @Test
    void countsErrorsForNullEntities() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        when(accountRepository.findByIbanIsNotNull()).thenReturn(List.of());
        try (MockedStatic<BankCsvParser> mock = Mockito.mockStatic(BankCsvParser.class)) {
            mock.when(() -> BankCsvParser.parse(file, Map.of()))
                    .thenReturn(java.util.Arrays.asList((BankTransaction) null));
            when(repository.findAll()).thenReturn(List.of());
            when(repository.saveAll(List.of())).thenReturn(List.of());

            BankCsvImportStatsDTO stats = service.importCsv(file);

            assertEquals(0, stats.getImports());
            assertEquals(0, stats.getDuplicates());
            assertEquals(1, stats.getErrors());
        }
    }

    @Test
    void importsNewTransactions() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Account account = new Account();
        account.setId(1L);
        account.setIban("DE");
        when(accountRepository.findByIbanIsNotNull()).thenReturn(List.of(account));
        BankTransaction t1 = new BankTransaction();
        t1.setAccount(account);
        t1.setBookingDate(java.time.LocalDate.now());
        t1.setPurpose("p1");
        t1.setCounterparty("c1");
        t1.setAmount(1L);
        t1.setData("d1");
        BankTransaction t2 = new BankTransaction();
        t2.setAccount(account);
        t2.setBookingDate(java.time.LocalDate.now());
        t2.setPurpose("p2");
        t2.setCounterparty("c2");
        t2.setAmount(2L);
        t2.setData("d2");
        try (MockedStatic<BankCsvParser> mock = Mockito.mockStatic(BankCsvParser.class)) {
            mock.when(() -> BankCsvParser.parse(file, Map.of("DE", account))).thenReturn(List.of(t1, t2));
            when(repository.findAll()).thenReturn(List.of());
            when(repository.saveAll(List.of(t1, t2))).thenReturn(List.of(t1, t2));

            BankCsvImportStatsDTO stats = service.importCsv(file);

            assertEquals(2, stats.getImports());
            assertEquals(0, stats.getDuplicates());
            assertEquals(0, stats.getErrors());
            verify(repository).saveAll(List.of(t1, t2));
            verify(suggestionService).updateAllFor(List.of(t1, t2), null);
        }
    }

    @Test
    void detectsDuplicatesByFieldsWithoutSameData() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Account account = new Account();
        account.setId(1L);
        account.setIban("DE");
        when(accountRepository.findByIbanIsNotNull()).thenReturn(List.of(account));
        BankTransaction existing = new BankTransaction();
        existing.setAccount(account);
        existing.setBookingDate(java.time.LocalDate.now());
        existing.setPurpose("p1");
        existing.setCounterparty("c1");
        existing.setAmount(1L);
        existing.setData("old");
        BankTransaction incoming = new BankTransaction();
        incoming.setAccount(account);
        incoming.setBookingDate(existing.getBookingDate());
        incoming.setPurpose("p1");
        incoming.setCounterparty("c1");
        incoming.setAmount(1L);
        incoming.setData("newData");
        try (MockedStatic<BankCsvParser> mock = Mockito.mockStatic(BankCsvParser.class)) {
            mock.when(() -> BankCsvParser.parse(file, Map.of("DE", account))).thenReturn(List.of(incoming));
            when(repository.findAll()).thenReturn(List.of(existing));
            when(repository.saveAll(List.of())).thenReturn(List.of());

            BankCsvImportStatsDTO stats = service.importCsv(file);

            assertEquals(0, stats.getImports());
            assertEquals(1, stats.getDuplicates());
            assertEquals(0, stats.getErrors());
        }
    }

    private static java.util.stream.Stream<org.junit.jupiter.params.provider.Arguments> exceptionProvider() {
        return java.util.stream.Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(new IOException("io"), IOException.class),
                org.junit.jupiter.params.provider.Arguments.of(
                        new IllegalArgumentException("iae"), IllegalArgumentException.class));
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.MethodSource("exceptionProvider")
    void propagatesParserExceptions(Exception toThrow, Class<? extends Exception> expected) throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        when(accountRepository.findByIbanIsNotNull()).thenReturn(List.of());
        try (MockedStatic<BankCsvParser> mock = Mockito.mockStatic(BankCsvParser.class)) {
            mock.when(() -> BankCsvParser.parse(file, Map.of())).thenThrow(toThrow);

            org.junit.jupiter.api.Assertions.assertThrows(expected, () -> service.importCsv(file));
        }
    }
}
