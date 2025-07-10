package com.lennartmoeller.finance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.TransactionLinkSuggestionDTO;
import com.lennartmoeller.finance.mapper.TransactionLinkSuggestionMapper;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.model.TransactionLinkState;
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
                bankTransactionRepository, mapper, repository, transactionRepository);
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
        account.setId(10L);
        account.setIban("DE");

        BankTransaction bank = new BankTransaction();
        bank.setId(1L);
        bank.setAccount(account);
        bank.setAmount(100L);
        bank.setBookingDate(LocalDate.of(2024, 1, 2));

        Transaction t1 = new Transaction();
        t1.setId(2L);
        t1.setAccount(account);
        t1.setAmount(100L);
        t1.setDate(LocalDate.of(2024, 1, 2));

        Transaction t2 = new Transaction();
        t2.setId(3L);
        t2.setAccount(account);
        t2.setAmount(100L);
        t2.setDate(LocalDate.of(2024, 1, 5));

        when(bankTransactionRepository.findAll()).thenReturn(List.of(bank));
        when(transactionRepository.findAll()).thenReturn(List.of(t1, t2));
        when(repository.findAllByBankTransactionIdsAndTransactionIds(any(), any()))
                .thenReturn(List.of());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<TransactionLinkSuggestion>> captor = ArgumentCaptor.forClass(List.class);
        when(repository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toDto(any())).thenReturn(new TransactionLinkSuggestionDTO());

        List<TransactionLinkSuggestionDTO> result = service.generateSuggestions(null, null);

        assertEquals(2, result.size());
        verify(repository).saveAll(captor.capture());
        List<TransactionLinkSuggestion> saved = captor.getValue();
        double p1 = saved.getFirst().getProbability();
        double p2 = saved.get(1).getProbability();
        boolean firstMatch = Math.abs(p1 - 1.0) < 1e-6 && Math.abs(p2 - (11.0 / 14.0)) < 1e-6;
        boolean secondMatch = Math.abs(p2 - 1.0) < 1e-6 && Math.abs(p1 - (11.0 / 14.0)) < 1e-6;
        assertTrue(firstMatch || secondMatch);

        TransactionLinkSuggestion confirmed = saved.stream()
                .filter(s -> Math.abs(s.getProbability() - 1.0) < 1e-6)
                .findFirst()
                .orElseThrow();
        assertEquals(TransactionLinkState.AUTO_CONFIRMED, confirmed.getLinkState());
    }

    @Test
    void testGenerateSuggestionsSkipsExisting() {
        Account account = new Account();
        account.setId(20L);
        account.setIban("DE");

        BankTransaction bank = new BankTransaction();
        bank.setId(21L);
        bank.setAccount(account);
        bank.setAmount(50L);
        bank.setBookingDate(LocalDate.of(2024, 2, 2));

        Transaction transaction = new Transaction();
        transaction.setId(22L);
        transaction.setAccount(account);
        transaction.setAmount(50L);
        transaction.setDate(LocalDate.of(2024, 2, 3));

        when(bankTransactionRepository.findAll()).thenReturn(List.of(bank));
        when(transactionRepository.findAll()).thenReturn(List.of(transaction));
        TransactionLinkSuggestion existing = new TransactionLinkSuggestion();
        existing.setBankTransaction(bank);
        existing.setTransaction(transaction);
        when(repository.findAllByBankTransactionIdsAndTransactionIds(any(), any()))
                .thenReturn(List.of(existing));

        List<TransactionLinkSuggestionDTO> result = service.generateSuggestions(null, null);

        assertEquals(0, result.size());
        verify(repository, never()).saveAll(any());
    }

    @Test
    void testGenerateSuggestionsHandlesLazyAccount() {
        class LazyAccount extends Account {
            @Override
            public boolean equals(Object obj) {
                throw new org.hibernate.LazyInitializationException("No session");
            }
        }

        LazyAccount account = new LazyAccount();
        account.setId(30L);
        account.setIban("DE");

        BankTransaction bank = new BankTransaction();
        bank.setId(31L);
        bank.setAccount(account);
        bank.setAmount(100L);
        bank.setBookingDate(LocalDate.of(2024, 3, 3));

        Transaction transaction = new Transaction();
        transaction.setId(32L);
        transaction.setAccount(account);
        transaction.setAmount(100L);
        transaction.setDate(LocalDate.of(2024, 3, 3));

        when(bankTransactionRepository.findAll()).thenReturn(List.of(bank));
        when(transactionRepository.findAll()).thenReturn(List.of(transaction));
        when(repository.findAllByBankTransactionIdsAndTransactionIds(any(), any()))
                .thenReturn(List.of());
        when(repository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toDto(any())).thenReturn(new TransactionLinkSuggestionDTO());

        List<TransactionLinkSuggestionDTO> result = service.generateSuggestions(null, null);

        assertEquals(1, result.size());
    }

    @Test
    void testUpdateForTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(40L);

        BankTransaction bank = new BankTransaction();
        bank.setId(41L);

        TransactionLinkSuggestion undecided = new TransactionLinkSuggestion();
        undecided.setLinkState(TransactionLinkState.UNDECIDED);
        undecided.setBankTransaction(bank);
        undecided.setTransaction(transaction);
        TransactionLinkSuggestion confirmed = new TransactionLinkSuggestion();
        confirmed.setLinkState(TransactionLinkState.CONFIRMED);
        confirmed.setBankTransaction(bank);
        confirmed.setTransaction(transaction);

        when(repository.findAllByBankTransactionIdsAndTransactionIds(null, List.of(40L)))
                .thenReturn(List.of(undecided, confirmed));
        when(bankTransactionRepository.findAll()).thenReturn(List.of());

        service.updateForTransactions(List.of(transaction));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<TransactionLinkSuggestion>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).deleteAll(captor.capture());
        assertEquals(List.of(undecided), captor.getValue());
    }

    @Test
    void testUpdateForBankTransaction() {
        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setId(50L);
        bankTransaction.setBookingDate(LocalDate.now());
        bankTransaction.setAmount(1L);
        Account account = new Account();
        account.setId(1L);
        bankTransaction.setAccount(account);

        Transaction tx = new Transaction();
        tx.setId(51L);

        TransactionLinkSuggestion auto = new TransactionLinkSuggestion();
        auto.setLinkState(TransactionLinkState.AUTO_CONFIRMED);
        auto.setBankTransaction(bankTransaction);
        auto.setTransaction(tx);
        TransactionLinkSuggestion rejected = new TransactionLinkSuggestion();
        rejected.setLinkState(TransactionLinkState.REJECTED);
        rejected.setBankTransaction(bankTransaction);
        rejected.setTransaction(tx);

        when(repository.findAllByBankTransactionIdsAndTransactionIds(List.of(50L), null))
                .thenReturn(List.of(auto, rejected));
        when(transactionRepository.findAll()).thenReturn(List.of());

        service.updateForBankTransactions(List.of(bankTransaction));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<TransactionLinkSuggestion>> captor2 = ArgumentCaptor.forClass(List.class);
        verify(repository).deleteAll(captor2.capture());
        assertEquals(List.of(auto), captor2.getValue());
    }

    @Test
    void testGenerateSuggestionsUsesProvidedLists() {
        Account account = new Account();
        account.setId(1L);

        Transaction t = new Transaction();
        t.setId(1L);
        t.setAccount(account);
        t.setAmount(5L);
        t.setDate(LocalDate.now());

        BankTransaction b = new BankTransaction();
        b.setId(2L);
        b.setAccount(account);
        b.setAmount(5L);
        b.setBookingDate(LocalDate.now());
        when(repository.findAllByBankTransactionIdsAndTransactionIds(List.of(2L), List.of(1L)))
                .thenReturn(List.of());
        when(repository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toDto(any())).thenReturn(new TransactionLinkSuggestionDTO());

        List<TransactionLinkSuggestionDTO> result = service.generateSuggestions(List.of(t), List.of(b));

        assertEquals(1, result.size());
        verifyNoInteractions(bankTransactionRepository, transactionRepository);
    }

    @Test
    void testUpdateForTransactionsNoInput() {
        service.updateForTransactions(null);
        service.updateForTransactions(List.of());
        verifyNoInteractions(repository);
    }

    @Test
    void testUpdateForBankTransactionsNoInput() {
        service.updateForBankTransactions(null);
        service.updateForBankTransactions(List.of());
        verifyNoInteractions(repository);
    }

    @Test
    void testRemoveMethods() {
        service.removeForTransaction(1L);
        service.removeForBankTransaction(2L);
        verify(repository).deleteAllByTransaction_Id(1L);
        verify(repository).deleteAllByBankTransaction_Id(2L);
    }

    @Test
    void testFindByIdFound() {
        TransactionLinkSuggestion suggestion = new TransactionLinkSuggestion();
        TransactionLinkSuggestionDTO dto = new TransactionLinkSuggestionDTO();
        when(repository.findById(10L)).thenReturn(java.util.Optional.of(suggestion));
        when(mapper.toDto(suggestion)).thenReturn(dto);

        var result = service.findById(10L);

        assertTrue(result.isPresent());
        assertEquals(dto, result.get());
    }

    @Test
    void testFindByIdNotFound() {
        when(repository.findById(11L)).thenReturn(java.util.Optional.empty());

        var result = service.findById(11L);

        assertTrue(result.isEmpty());
        verifyNoInteractions(mapper);
    }

    @Test
    void testUpdateLinkState() {
        BankTransaction bank = new BankTransaction();
        bank.setId(70L);
        Transaction tx = new Transaction();
        tx.setId(71L);

        TransactionLinkSuggestion suggestion = new TransactionLinkSuggestion();
        suggestion.setBankTransaction(bank);
        suggestion.setTransaction(tx);
        TransactionLinkSuggestion saved = new TransactionLinkSuggestion();
        saved.setBankTransaction(bank);
        saved.setTransaction(tx);
        TransactionLinkSuggestionDTO dto = new TransactionLinkSuggestionDTO();
        when(repository.findById(12L)).thenReturn(java.util.Optional.of(suggestion));
        when(repository.save(suggestion)).thenReturn(saved);
        when(repository.findAllByBankTransactionIdsOrTransactionIds(any(), any()))
                .thenReturn(List.of(saved));
        when(repository.saveAll(any())).thenReturn(List.of(saved));
        when(mapper.toDto(saved)).thenReturn(dto);

        var result = service.updateLinkState(12L, TransactionLinkState.CONFIRMED);

        assertTrue(result.isPresent());
        assertEquals(dto, result.get());
        assertEquals(TransactionLinkState.CONFIRMED, suggestion.getLinkState());
    }

    @Test
    void testUpdateLinkStateEnforcesAutoReject() {
        BankTransaction bank = new BankTransaction();
        bank.setId(100L);
        Transaction t1 = new Transaction();
        t1.setId(101L);
        Transaction t2 = new Transaction();
        t2.setId(102L);

        TransactionLinkSuggestion toConfirm = new TransactionLinkSuggestion();
        toConfirm.setId(1L);
        toConfirm.setBankTransaction(bank);
        toConfirm.setTransaction(t1);
        toConfirm.setLinkState(TransactionLinkState.UNDECIDED);

        TransactionLinkSuggestion other = new TransactionLinkSuggestion();
        other.setId(2L);
        other.setBankTransaction(bank);
        other.setTransaction(t2);
        other.setLinkState(TransactionLinkState.UNDECIDED);

        when(repository.findById(1L)).thenReturn(java.util.Optional.of(toConfirm));
        when(repository.save(toConfirm)).thenReturn(toConfirm);
        when(repository.findAllByBankTransactionIdsOrTransactionIds(List.of(100L), List.of(101L)))
                .thenReturn(List.of(toConfirm, other));
        when(repository.saveAll(List.of(other))).thenReturn(List.of(other));
        when(mapper.toDto(toConfirm)).thenReturn(new TransactionLinkSuggestionDTO());

        service.updateLinkState(1L, TransactionLinkState.CONFIRMED);

        assertEquals(TransactionLinkState.CONFIRMED, toConfirm.getLinkState());
        assertEquals(TransactionLinkState.AUTO_REJECTED, other.getLinkState());
    }

    @Test
    void testUpdateLinkStateRestoresAutoRejected() {
        BankTransaction bank = new BankTransaction();
        bank.setId(200L);
        Transaction t1 = new Transaction();
        t1.setId(201L);
        Transaction t2 = new Transaction();
        t2.setId(202L);

        TransactionLinkSuggestion confirmed = new TransactionLinkSuggestion();
        confirmed.setId(3L);
        confirmed.setBankTransaction(bank);
        confirmed.setTransaction(t1);
        confirmed.setProbability(0.5);
        confirmed.setLinkState(TransactionLinkState.CONFIRMED);

        TransactionLinkSuggestion auto1 = new TransactionLinkSuggestion();
        auto1.setId(4L);
        auto1.setBankTransaction(bank);
        auto1.setTransaction(t2);
        auto1.setProbability(1.0);
        auto1.setLinkState(TransactionLinkState.AUTO_REJECTED);

        when(repository.findById(3L)).thenReturn(java.util.Optional.of(confirmed));
        when(repository.save(confirmed)).thenReturn(confirmed);
        when(repository.findAllByBankTransactionIdsOrTransactionIds(List.of(200L), List.of(201L)))
                .thenReturn(List.of(confirmed, auto1));
        when(repository.saveAll(List.of(auto1))).thenReturn(List.of(auto1));
        when(mapper.toDto(confirmed)).thenReturn(new TransactionLinkSuggestionDTO());

        service.updateLinkState(3L, TransactionLinkState.REJECTED);

        assertEquals(TransactionLinkState.REJECTED, confirmed.getLinkState());
        assertEquals(TransactionLinkState.AUTO_CONFIRMED, auto1.getLinkState());
    }
}
