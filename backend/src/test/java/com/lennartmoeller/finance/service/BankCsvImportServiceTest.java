package com.lennartmoeller.finance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
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
import org.springframework.web.multipart.MultipartFile;

class BankCsvImportServiceTest {
    private BankTransactionRepository repository;
    private AccountRepository accountRepository;
    private TransactionLinkSuggestionService suggestionService;
    private BankCsvImportService service;

    @BeforeEach
    void setUp() {
        repository = mock(BankTransactionRepository.class);
        accountRepository = mock(AccountRepository.class);
        suggestionService = mock(TransactionLinkSuggestionService.class);
        service = new BankCsvImportService(accountRepository, repository, suggestionService);
    }

    @Test
    void importsNewTransactions() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        Account account = new Account();
        account.setId(1L);
        account.setIban("DE");
        BankTransaction entity = new BankTransaction();
        entity.setAccount(account);
        when(accountRepository.findByIbanIsNotNull()).thenReturn(List.of(account));
        when(repository.findAll()).thenReturn(List.of());
        try (MockedStatic<BankCsvParser> mocked = org.mockito.Mockito.mockStatic(BankCsvParser.class)) {
            mocked.when(() -> BankCsvParser.parse(file, Map.of("DE", account))).thenReturn(List.of(entity));
            when(repository.saveAll(List.of(entity))).thenReturn(List.of(entity));

            BankCsvImportStatsDTO stats = service.importCsv(file);

            assertThat(stats.getImports()).isEqualTo(1);
            assertThat(stats.getDuplicates()).isZero();
            assertThat(stats.getErrors()).isZero();
            verify(repository).saveAll(List.of(entity));
            verify(suggestionService).updateAllFor(List.of(entity), null);
        }
    }

    @Test
    void countsDuplicates() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        Account account = new Account();
        account.setId(1L);
        account.setIban("DE");
        BankTransaction existing = new BankTransaction();
        existing.setAccount(account);
        existing.setData("d");
        BankTransaction parsed = new BankTransaction();
        parsed.setAccount(account);
        parsed.setData("d");
        when(accountRepository.findByIbanIsNotNull()).thenReturn(List.of(account));
        when(repository.findAll()).thenReturn(List.of(existing));
        try (MockedStatic<BankCsvParser> mocked = org.mockito.Mockito.mockStatic(BankCsvParser.class)) {
            mocked.when(() -> BankCsvParser.parse(file, Map.of("DE", account))).thenReturn(List.of(parsed));
            when(repository.saveAll(List.of())).thenReturn(List.of());

            BankCsvImportStatsDTO stats = service.importCsv(file);

            assertThat(stats.getImports()).isZero();
            assertThat(stats.getDuplicates()).isEqualTo(1);
            assertThat(stats.getErrors()).isZero();
            verify(repository).saveAll(List.of());
            verify(suggestionService).updateAllFor(List.of(), null);
        }
    }

    @Test
    void countsErrorsForNullEntries() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        Account account = new Account();
        account.setId(1L);
        account.setIban("DE");
        when(accountRepository.findByIbanIsNotNull()).thenReturn(List.of(account));
        when(repository.findAll()).thenReturn(List.of());
        try (MockedStatic<BankCsvParser> mocked = org.mockito.Mockito.mockStatic(BankCsvParser.class)) {
            mocked.when(() -> BankCsvParser.parse(file, Map.of("DE", account)))
                    .thenReturn(java.util.Arrays.asList((BankTransaction) null));
            when(repository.saveAll(List.of())).thenReturn(List.of());

            BankCsvImportStatsDTO stats = service.importCsv(file);

            assertThat(stats.getImports()).isZero();
            assertThat(stats.getDuplicates()).isZero();
            assertThat(stats.getErrors()).isEqualTo(1);
        }
    }
}
