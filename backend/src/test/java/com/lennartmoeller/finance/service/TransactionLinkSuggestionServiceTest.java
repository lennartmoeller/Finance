package com.lennartmoeller.finance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.lennartmoeller.finance.dto.TransactionLinkSuggestionDTO;
import com.lennartmoeller.finance.mapper.TransactionLinkSuggestionMapper;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.model.TransactionLinkSuggestion;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import com.lennartmoeller.finance.repository.TransactionLinkSuggestionRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class TransactionLinkSuggestionServiceTest {
    private TransactionLinkSuggestionRepository repository;
    private TransactionLinkSuggestionMapper mapper;
    private BankTransactionRepository bankTransactionRepository;
    private TransactionRepository transactionRepository;
    private TransactionLinkSuggestionService service;

    @BeforeEach
    void setUp() {
        repository = mock(TransactionLinkSuggestionRepository.class);
        mapper = mock(TransactionLinkSuggestionMapper.class);
        bankTransactionRepository = mock(BankTransactionRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        service = new TransactionLinkSuggestionService(
                repository, mapper, bankTransactionRepository, transactionRepository);
    }

    @Test
    void testFindAll() {
        TransactionLinkSuggestion s1 = new TransactionLinkSuggestion();
        TransactionLinkSuggestion s2 = new TransactionLinkSuggestion();
        when(repository.findAll()).thenReturn(List.of(s1, s2));

        TransactionLinkSuggestionDTO d1 = new TransactionLinkSuggestionDTO();
        TransactionLinkSuggestionDTO d2 = new TransactionLinkSuggestionDTO();
        when(mapper.toDto(any(TransactionLinkSuggestion.class))).thenReturn(d1, d2);

        List<TransactionLinkSuggestionDTO> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals(d1, result.getFirst());
        assertEquals(d2, result.get(1));
        verify(repository).findAll();
        verify(mapper, times(2)).toDto(any(TransactionLinkSuggestion.class));
    }

    @Test
    void testGenerateSuggestions() {
        Account account = new Account();
        account.setIban("DE");

        BankTransaction bank = new BankTransaction();
        bank.setAccount(account);
        bank.setAmount(100L);
        bank.setBookingDate(LocalDate.of(2024, 1, 2));

        Transaction t1 = new Transaction();
        t1.setAccount(account);
        t1.setAmount(100L);
        t1.setDate(LocalDate.of(2024, 1, 2));

        Transaction t2 = new Transaction();
        t2.setAccount(account);
        t2.setAmount(100L);
        t2.setDate(LocalDate.of(2024, 1, 5));

        when(bankTransactionRepository.findAll()).thenReturn(List.of(bank));
        when(transactionRepository.findAll()).thenReturn(List.of(t1, t2));
        when(repository.existsByBankTransactionAndTransaction(bank, t1)).thenReturn(false);
        when(repository.existsByBankTransactionAndTransaction(bank, t2)).thenReturn(false);

        ArgumentCaptor<TransactionLinkSuggestion> captor = ArgumentCaptor.forClass(TransactionLinkSuggestion.class);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toDto(any())).thenReturn(new TransactionLinkSuggestionDTO());

        List<TransactionLinkSuggestionDTO> result = service.generateSuggestions(null, null);

        assertEquals(2, result.size());
        verify(repository, times(2)).save(captor.capture());

        List<TransactionLinkSuggestion> saved = captor.getAllValues();
        double p1 = saved.getFirst().getProbability();
        double p2 = saved.get(1).getProbability();
        boolean firstMatch = Math.abs(p1 - 1.0) < 1e-6 && Math.abs(p2 - (4.0 / 7)) < 1e-6;
        boolean secondMatch = Math.abs(p2 - 1.0) < 1e-6 && Math.abs(p1 - (4.0 / 7)) < 1e-6;
        assertEquals(true, firstMatch || secondMatch);
    }

    @Test
    void testGenerateSuggestionsSkipsExisting() {
        Account account = new Account();
        account.setIban("DE");

        BankTransaction bank = new BankTransaction();
        bank.setAccount(account);
        bank.setAmount(50L);
        bank.setBookingDate(LocalDate.of(2024, 2, 2));

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(50L);
        transaction.setDate(LocalDate.of(2024, 2, 3));

        when(bankTransactionRepository.findAll()).thenReturn(List.of(bank));
        when(transactionRepository.findAll()).thenReturn(List.of(transaction));
        when(repository.existsByBankTransactionAndTransaction(bank, transaction))
                .thenReturn(true);

        List<TransactionLinkSuggestionDTO> result = service.generateSuggestions(null, null);

        assertEquals(0, result.size());
        verify(repository, never()).save(any());
    }
}
