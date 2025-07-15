package com.lennartmoeller.finance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.TransactionDTO;
import com.lennartmoeller.finance.mapper.TransactionMapper;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.CategoryRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TransactionServiceTest {
    private CategoryService categoryService;
    private TransactionRepository transactionRepository;
    private TransactionMapper transactionMapper;
    private AccountRepository accountRepository;
    private CategoryRepository categoryRepository;
    private TransactionLinkSuggestionService suggestionService;
    private TransactionService service;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        categoryService = mock(CategoryService.class);
        transactionMapper = mock(TransactionMapper.class);
        transactionRepository = mock(TransactionRepository.class);
        suggestionService = mock(TransactionLinkSuggestionService.class);
        service = new TransactionService(
                accountRepository,
                categoryRepository,
                categoryService,
                suggestionService,
                transactionMapper,
                transactionRepository);
    }

    @Test
    void findFilteredSortsResults() {
        List<Long> accountIds = List.of(1L);
        List<Long> categoryIds = List.of(2L);
        List<Long> extendedCategoryIds = List.of(2L, 3L);
        List<YearMonth> months = List.of(YearMonth.of(2021, 1));
        List<String> monthStrings = List.of("2021-01");
        when(categoryService.collectChildCategoryIdsRecursively(categoryIds)).thenReturn(extendedCategoryIds);

        Transaction t1 = new Transaction();
        t1.setId(2L);
        t1.setDate(LocalDate.of(2021, 1, 10));
        Transaction t2 = new Transaction();
        t2.setId(1L);
        t2.setDate(LocalDate.of(2021, 1, 10));
        Transaction t3 = new Transaction();
        t3.setId(3L);
        t3.setDate(LocalDate.of(2021, 1, 11));
        when(transactionRepository.findFiltered(accountIds, extendedCategoryIds, monthStrings, null))
                .thenReturn(List.of(t1, t2, t3));

        TransactionDTO d1 = new TransactionDTO();
        TransactionDTO d2 = new TransactionDTO();
        TransactionDTO d3 = new TransactionDTO();
        when(transactionMapper.toDto(t1)).thenReturn(d1);
        when(transactionMapper.toDto(t2)).thenReturn(d2);
        when(transactionMapper.toDto(t3)).thenReturn(d3);

        List<TransactionDTO> result = service.findFiltered(accountIds, categoryIds, months, null);

        assertEquals(List.of(d2, d1, d3), result); // sorted by date then id
        verify(transactionRepository).findFiltered(accountIds, extendedCategoryIds, monthStrings, null);
    }

    @Test
    void findFilteredReturnsEmptyWhenParametersAreNull() {
        when(categoryService.collectChildCategoryIdsRecursively(null)).thenReturn(null);
        when(transactionRepository.findFiltered(null, null, null, null)).thenReturn(List.of());

        List<TransactionDTO> result = service.findFiltered(null, null, null, null);

        assertTrue(result.isEmpty());
    }

    @Nested
    class FindById {
        @ParameterizedTest
        @CsvSource({"7,true", "8,false"})
        void returnsOptionalDependingOnRepository(long id, boolean exists) {
            Transaction entity = new Transaction();
            TransactionDTO dto = new TransactionDTO();

            if (exists) {
                when(transactionRepository.findById(id)).thenReturn(Optional.of(entity));
                when(transactionMapper.toDto(entity)).thenReturn(dto);
            } else {
                when(transactionRepository.findById(id)).thenReturn(Optional.empty());
            }

            Optional<TransactionDTO> result = service.findById(id);

            assertEquals(exists, result.isPresent());
            if (exists) {
                assertEquals(dto, result.get());
            }
        }
    }

    @Test
    void saveMapsAndPersistsEntity() {
        TransactionDTO dtoIn = new TransactionDTO();
        Transaction entity = new Transaction();
        Transaction saved = new Transaction();
        TransactionDTO dtoOut = new TransactionDTO();

        when(transactionMapper.toEntity(dtoIn, accountRepository, categoryRepository))
                .thenReturn(entity);
        when(transactionRepository.save(entity)).thenReturn(saved);
        when(transactionMapper.toDto(saved)).thenReturn(dtoOut);

        TransactionDTO result = service.save(dtoIn);

        assertEquals(dtoOut, result);
        verify(transactionMapper).toEntity(dtoIn, accountRepository, categoryRepository);
    }

    @Test
    void deleteByIdRemovesEntityAndSuggestions() {
        service.deleteById(11L);
        verify(suggestionService).removeForTransaction(11L);
        verify(transactionRepository).deleteById(11L);
    }

    @Test
    void saveTriggersSuggestionUpdate() {
        TransactionDTO dto = new TransactionDTO();
        Transaction entity = new Transaction();
        Transaction saved = new Transaction();
        when(transactionMapper.toEntity(dto, accountRepository, categoryRepository))
                .thenReturn(entity);
        when(transactionRepository.save(entity)).thenReturn(saved);
        when(transactionMapper.toDto(saved)).thenReturn(dto);

        service.save(dto);

        verify(suggestionService).updateAllFor(null, List.of(saved));
    }
}
