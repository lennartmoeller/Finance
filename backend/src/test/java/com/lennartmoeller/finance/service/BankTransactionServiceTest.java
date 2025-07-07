package com.lennartmoeller.finance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.mapper.BankTransactionMapper;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BankTransactionServiceTest {
    private BankTransactionRepository repository;
    private BankTransactionMapper mapper;
    private AccountRepository accountRepository;
    private TransactionLinkSuggestionService suggestionService;
    private BankTransactionService service;

    @BeforeEach
    void setUp() {
        repository = mock(BankTransactionRepository.class);
        mapper = mock(BankTransactionMapper.class);
        accountRepository = mock(AccountRepository.class);
        suggestionService = mock(TransactionLinkSuggestionService.class);
        service = new BankTransactionService(repository, mapper, accountRepository, suggestionService);
    }

    @Test
    void testFindAll() {
        BankTransaction t = new BankTransaction();
        BankTransactionDTO dto = new BankTransactionDTO();
        when(repository.findAll()).thenReturn(List.of(t));
        when(mapper.toDto(t)).thenReturn(dto);

        List<BankTransactionDTO> result = service.findAll();

        assertEquals(List.of(dto), result);
    }

    @Test
    void testFindByIdFound() {
        BankTransaction t = new BankTransaction();
        BankTransactionDTO dto = new BankTransactionDTO();
        when(repository.findById(7L)).thenReturn(Optional.of(t));
        when(mapper.toDto(t)).thenReturn(dto);

        Optional<BankTransactionDTO> result = service.findById(7L);

        assertTrue(result.isPresent());
        assertEquals(dto, result.get());
    }

    @Test
    void testFindByIdNotFound() {
        when(repository.findById(8L)).thenReturn(Optional.empty());

        Optional<BankTransactionDTO> result = service.findById(8L);

        assertTrue(result.isEmpty());
        verifyNoInteractions(mapper);
    }

    @Test
    void testSave() {
        BankTransactionDTO dtoIn = new BankTransactionDTO();
        dtoIn.setIban("DE");
        BankTransaction entity = new BankTransaction();
        BankTransaction saved = new BankTransaction();
        BankTransactionDTO dtoOut = new BankTransactionDTO();
        Account account = new Account();
        when(accountRepository.findAllByIbanIn(Collections.singleton("DE"))).thenReturn(List.of(account));
        when(mapper.toEntity(dtoIn, account)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(dtoOut);

        BankTransactionDTO result = service.save(dtoIn);

        assertEquals(dtoOut, result);
        verify(suggestionService).updateForBankTransactions(List.of(saved));
    }

    @Test
    void testSaveWithoutIban() {
        BankTransactionDTO dto = new BankTransactionDTO();
        BankTransaction entity = new BankTransaction();
        BankTransaction saved = new BankTransaction();

        when(mapper.toEntity(dto, null)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(dto);

        BankTransactionDTO result = service.save(dto);

        assertEquals(dto, result);
        verifyNoInteractions(accountRepository);
        verify(suggestionService).updateForBankTransactions(List.of(saved));
    }

    @Test
    void testSaveWithIbanNotFound() {
        BankTransactionDTO dto = new BankTransactionDTO();
        dto.setIban("DE");
        BankTransaction entity = new BankTransaction();
        BankTransaction saved = new BankTransaction();

        when(accountRepository.findAllByIbanIn(Collections.singleton("DE"))).thenReturn(List.of());
        when(mapper.toEntity(dto, null)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(dto);

        BankTransactionDTO result = service.save(dto);

        assertEquals(dto, result);
        verify(accountRepository).findAllByIbanIn(Collections.singleton("DE"));
        verify(suggestionService).updateForBankTransactions(List.of(saved));
    }

    @Test
    void testDeleteById() {
        service.deleteById(11L);
        verify(suggestionService).removeForBankTransaction(11L);
        verify(repository).deleteById(11L);
    }
}
