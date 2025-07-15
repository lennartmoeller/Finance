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
}
