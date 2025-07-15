package com.lennartmoeller.finance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.csv.BankCsvParser;
import com.lennartmoeller.finance.dto.BankCsvImportStatsDTO;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import com.lennartmoeller.finance.testbuilder.BankTransactionBuilder;
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

    private static Account account(long id) {
        Account a = new Account();
        a.setId(id);
        a.setIban("DE" + id);
        return a;
    }

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(BankTransactionRepository.class);
        accountRepository = Mockito.mock(AccountRepository.class);
        suggestionService = Mockito.mock(TransactionLinkSuggestionService.class);
        service = new BankCsvImportService(accountRepository, repository, suggestionService);
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.MethodSource("duplicateTransactions")
    void countsDuplicates(BankTransaction existing, BankTransaction incoming) throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Account account = existing.getAccount();
        when(accountRepository.findByIbanIsNotNull()).thenReturn(List.of(account));
        try (MockedStatic<BankCsvParser> mock = Mockito.mockStatic(BankCsvParser.class)) {
            mock.when(() -> BankCsvParser.parse(file, Map.of(account.getIban(), account)))
                    .thenReturn(List.of(incoming));
            when(repository.findAll()).thenReturn(List.of(existing));
            when(repository.saveAll(List.of())).thenReturn(List.of());

            BankCsvImportStatsDTO stats = service.importCsv(file);

            assertThat(stats.getImports()).isZero();
            assertThat(stats.getDuplicates()).isEqualTo(1);
            assertThat(stats.getErrors()).isZero();
            verify(repository).saveAll(List.of());
            verify(suggestionService).updateAllFor(List.of(), null);
        }
    }

    private static java.util.stream.Stream<org.junit.jupiter.params.provider.Arguments> duplicateTransactions() {
        java.time.LocalDate today = java.time.LocalDate.now();
        Account a1 = account(1L);
        BankTransaction existingSameData = BankTransactionBuilder.aBankTransaction()
                .withAccount(a1)
                .withBookingDate(today)
                .withPurpose("p")
                .withCounterparty("c")
                .withAmount(1L)
                .withData("d1")
                .build();
        BankTransaction incomingSameData = BankTransactionBuilder.aBankTransaction()
                .withAccount(a1)
                .withBookingDate(today.plusDays(1))
                .withPurpose("other")
                .withCounterparty("other")
                .withAmount(2L)
                .withData("d1")
                .build();

        Account a2 = account(2L);
        BankTransaction existingFields = BankTransactionBuilder.aBankTransaction()
                .withAccount(a2)
                .withBookingDate(today)
                .withPurpose("p1")
                .withCounterparty("c1")
                .withAmount(1L)
                .withData("old")
                .build();
        BankTransaction incomingFields = BankTransactionBuilder.aBankTransaction()
                .withAccount(a2)
                .withBookingDate(today)
                .withPurpose("p1")
                .withCounterparty("c1")
                .withAmount(1L)
                .withData("newData")
                .build();

        return java.util.stream.Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(existingSameData, incomingSameData),
                org.junit.jupiter.params.provider.Arguments.of(existingFields, incomingFields));
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

            assertThat(stats.getImports()).isZero();
            assertThat(stats.getDuplicates()).isZero();
            assertThat(stats.getErrors()).isEqualTo(1);
        }
    }

    @Test
    void importsNewTransactions() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Account account = account(1L);
        when(accountRepository.findByIbanIsNotNull()).thenReturn(List.of(account));
        BankTransaction t1 = BankTransactionBuilder.aBankTransaction()
                .withAccount(account)
                .withBookingDate(java.time.LocalDate.now())
                .withPurpose("p1")
                .withCounterparty("c1")
                .withAmount(1L)
                .withData("d1")
                .build();
        BankTransaction t2 = BankTransactionBuilder.aBankTransaction()
                .withAccount(account)
                .withBookingDate(java.time.LocalDate.now())
                .withPurpose("p2")
                .withCounterparty("c2")
                .withAmount(2L)
                .withData("d2")
                .build();
        try (MockedStatic<BankCsvParser> mock = Mockito.mockStatic(BankCsvParser.class)) {
            mock.when(() -> BankCsvParser.parse(file, Map.of(account.getIban(), account)))
                    .thenReturn(List.of(t1, t2));
            when(repository.findAll()).thenReturn(List.of());
            when(repository.saveAll(List.of(t1, t2))).thenReturn(List.of(t1, t2));

            BankCsvImportStatsDTO stats = service.importCsv(file);

            assertThat(stats.getImports()).isEqualTo(2);
            assertThat(stats.getDuplicates()).isZero();
            assertThat(stats.getErrors()).isZero();
            verify(repository).saveAll(List.of(t1, t2));
            verify(suggestionService).updateAllFor(List.of(t1, t2), null);
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
