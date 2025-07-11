package com.lennartmoeller.finance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        when(repository.findAllByBankTransactionIdsOrTransactionIds(any(), any()))
                .thenReturn(List.of());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<TransactionLinkSuggestion>> captor = ArgumentCaptor.forClass(List.class);
        when(repository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toDto(any())).thenReturn(new TransactionLinkSuggestionDTO());

        List<TransactionLinkSuggestionDTO> result = service.updateAllFor(List.of(bank), List.of(t1, t2));

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
    void testUpdateAllForSkipsExisting() {
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
        when(repository.findAllByBankTransactionIdsOrTransactionIds(any(), any()))
                .thenReturn(new java.util.ArrayList<>(List.of(existing)));

        service.updateAllFor(List.of(bank), List.of(transaction));

        verify(repository).saveAll(any());
    }

    @Test
    void testUpdateAllForHandlesLazyAccount() {
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
        when(repository.findAllByBankTransactionIdsOrTransactionIds(any(), any()))
                .thenReturn(new java.util.ArrayList<>());
        when(repository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toDto(any())).thenReturn(new TransactionLinkSuggestionDTO());

        List<TransactionLinkSuggestionDTO> result = service.updateAllFor(List.of(bank), List.of(transaction));

        assertEquals(1, result.size());
    }

    @Test
    void testUpdateAllForTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(40L);

        BankTransaction bt = new BankTransaction();
        bt.setId(41L);
        TransactionLinkSuggestion undecided = new TransactionLinkSuggestion();
        undecided.setLinkState(TransactionLinkState.UNDECIDED);
        undecided.setBankTransaction(bt);
        undecided.setTransaction(transaction);
        TransactionLinkSuggestion confirmed = new TransactionLinkSuggestion();
        confirmed.setLinkState(TransactionLinkState.CONFIRMED);
        confirmed.setBankTransaction(bt);
        confirmed.setTransaction(transaction);

        when(repository.findAllByBankTransactionIdsOrTransactionIds(null, List.of(40L)))
                .thenReturn(new java.util.ArrayList<>(List.of(undecided, confirmed)));
        when(bankTransactionRepository.findAll()).thenReturn(List.of());

        service.updateAllFor(null, List.of(transaction));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<TransactionLinkSuggestion>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).deleteAll(any());
    }

    @Test
    void testUpdateAllForBankTransaction() {
        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setId(50L);
        bankTransaction.setBookingDate(LocalDate.now());
        bankTransaction.setAmount(1L);
        Account account = new Account();
        account.setId(1L);
        bankTransaction.setAccount(account);

        TransactionLinkSuggestion auto = new TransactionLinkSuggestion();
        auto.setLinkState(TransactionLinkState.AUTO_CONFIRMED);
        auto.setBankTransaction(bankTransaction);
        auto.setTransaction(new Transaction());
        TransactionLinkSuggestion rejected = new TransactionLinkSuggestion();
        rejected.setLinkState(TransactionLinkState.REJECTED);
        rejected.setBankTransaction(bankTransaction);
        rejected.setTransaction(new Transaction());

        when(repository.findAllByBankTransactionIdsOrTransactionIds(List.of(50L), null))
                .thenReturn(new java.util.ArrayList<>(List.of(auto, rejected)));
        when(transactionRepository.findAll()).thenReturn(List.of());

        service.updateAllFor(List.of(bankTransaction), null);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<TransactionLinkSuggestion>> captor2 = ArgumentCaptor.forClass(List.class);
        verify(repository).deleteAll(any());
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
        when(repository.findAllByBankTransactionIdsOrTransactionIds(List.of(2L), List.of(1L)))
                .thenReturn(new java.util.ArrayList<>());
        when(bankTransactionRepository.findAll()).thenReturn(List.of(b));
        when(transactionRepository.findAll()).thenReturn(List.of(t));
        when(repository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toDto(any())).thenReturn(new TransactionLinkSuggestionDTO());

        List<TransactionLinkSuggestionDTO> result = service.updateAllFor(List.of(b), List.of(t));

        assertEquals(1, result.size());
    }

    @Test
    void testUpdateAllForTransactionsNoInput() {
        service.updateAllFor(null, null);
        service.updateAllFor(null, List.of());
        verifyNoInteractions(repository);
    }

    @Test
    void testUpdateAllForBankTransactionsNoInput() {
        service.updateAllFor(null, null);
        service.updateAllFor(List.of(), null);
        verifyNoInteractions(repository);
    }

    @Test
    void testRemoveMethods() {
        service.removeForTransaction(1L);
        verify(repository).deleteAllByTransaction_Id(1L);
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
        TransactionLinkSuggestion suggestion = new TransactionLinkSuggestion();
        BankTransaction sbt = new BankTransaction();
        sbt.setId(1L);
        suggestion.setBankTransaction(sbt);
        Transaction st = new Transaction();
        st.setId(1L);
        suggestion.setTransaction(st);
        TransactionLinkSuggestion saved = new TransactionLinkSuggestion();
        BankTransaction dbt = new BankTransaction();
        dbt.setId(1L);
        saved.setBankTransaction(dbt);
        Transaction dt = new Transaction();
        dt.setId(1L);
        saved.setTransaction(dt);
        TransactionLinkSuggestionDTO dto = new TransactionLinkSuggestionDTO();
        when(repository.findById(12L)).thenReturn(java.util.Optional.of(suggestion));
        when(repository.save(suggestion)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(dto);

        TransactionLinkSuggestionDTO result = service.updateLinkState(12L, TransactionLinkState.CONFIRMED);

        assertEquals(dto, result);
        assertEquals(TransactionLinkState.CONFIRMED, suggestion.getLinkState());
    }

    @Test
    void testUpdateLinkStateRejectsAutoStates() {
        assertThrows(
                IllegalArgumentException.class, () -> service.updateLinkState(1L, TransactionLinkState.AUTO_CONFIRMED));
    }

    @Test
    void updateLinkStateRejectsOtherSuggestionsWhenOneConfirmedExists() {
        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setId(1L);
        Transaction t1 = new Transaction();
        t1.setId(1L);
        Transaction t2 = new Transaction();
        t2.setId(2L);

        TransactionLinkSuggestion toUpdate = new TransactionLinkSuggestion();
        toUpdate.setId(10L);
        toUpdate.setBankTransaction(bankTransaction);
        toUpdate.setTransaction(t1);
        toUpdate.setLinkState(TransactionLinkState.UNDECIDED);

        TransactionLinkSuggestion alreadyConfirmed = new TransactionLinkSuggestion();
        alreadyConfirmed.setId(11L);
        alreadyConfirmed.setBankTransaction(bankTransaction);
        alreadyConfirmed.setTransaction(t2);
        alreadyConfirmed.setLinkState(TransactionLinkState.CONFIRMED);

        TransactionLinkSuggestion undecided = new TransactionLinkSuggestion();
        undecided.setId(12L);
        undecided.setBankTransaction(bankTransaction);
        undecided.setTransaction(t2);
        undecided.setLinkState(TransactionLinkState.UNDECIDED);

        when(repository.findById(10L)).thenReturn(java.util.Optional.of(toUpdate));
        when(repository.findAllByBankTransactionIdsOrTransactionIds(List.of(1L), List.of(1L)))
                .thenReturn(List.of(alreadyConfirmed))
                .thenReturn(List.of(toUpdate, alreadyConfirmed, undecided));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toDto(any())).thenReturn(new TransactionLinkSuggestionDTO());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<TransactionLinkSuggestion>> captor = ArgumentCaptor.forClass(List.class);

        service.updateLinkState(10L, TransactionLinkState.CONFIRMED);

        verify(repository).save(alreadyConfirmed);
        verify(repository).save(toUpdate);
        verify(repository).saveAll(captor.capture());

        List<TransactionLinkSuggestion> saved = captor.getValue();
        assertEquals(1, saved.size());
        assertTrue(saved.contains(undecided));
        assertEquals(TransactionLinkState.AUTO_REJECTED, undecided.getLinkState());
    }

    @Test
    void updateLinkStateAutoConfirmedDominatesWhenNoConfirmedSuggestions() {
        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setId(2L);
        Transaction t1 = new Transaction();
        t1.setId(3L);
        Transaction t2 = new Transaction();
        t2.setId(4L);

        TransactionLinkSuggestion toUpdate = new TransactionLinkSuggestion();
        toUpdate.setId(20L);
        toUpdate.setBankTransaction(bankTransaction);
        toUpdate.setTransaction(t1);
        toUpdate.setLinkState(TransactionLinkState.CONFIRMED);

        TransactionLinkSuggestion auto = new TransactionLinkSuggestion();
        auto.setId(21L);
        auto.setBankTransaction(bankTransaction);
        auto.setTransaction(t2);
        auto.setLinkState(TransactionLinkState.AUTO_CONFIRMED);

        TransactionLinkSuggestion undecided = new TransactionLinkSuggestion();
        undecided.setId(22L);
        undecided.setBankTransaction(bankTransaction);
        undecided.setTransaction(t2);
        undecided.setLinkState(TransactionLinkState.UNDECIDED);

        when(repository.findById(20L)).thenReturn(java.util.Optional.of(toUpdate));
        when(repository.findAllByBankTransactionIdsOrTransactionIds(List.of(2L), List.of(3L)))
                .thenReturn(List.of(toUpdate, auto, undecided));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toDto(any())).thenReturn(new TransactionLinkSuggestionDTO());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<TransactionLinkSuggestion>> captor = ArgumentCaptor.forClass(List.class);

        service.updateLinkState(20L, TransactionLinkState.UNDECIDED);

        verify(repository).save(toUpdate);
        verify(repository).saveAll(captor.capture());

        List<TransactionLinkSuggestion> saved = captor.getValue();
        assertEquals(2, saved.size());
        assertTrue(saved.contains(toUpdate));
        assertTrue(saved.contains(undecided));
        assertEquals(TransactionLinkState.AUTO_REJECTED, toUpdate.getLinkState());
        assertEquals(TransactionLinkState.AUTO_REJECTED, undecided.getLinkState());
    }

    @Test
    void updateLinkStateReturnsDtoWhenStateUnchanged() {
        TransactionLinkSuggestion suggestion = new TransactionLinkSuggestion();
        suggestion.setLinkState(TransactionLinkState.UNDECIDED);
        TransactionLinkSuggestionDTO dto = new TransactionLinkSuggestionDTO();

        when(repository.findById(99L)).thenReturn(java.util.Optional.of(suggestion));
        when(mapper.toDto(suggestion)).thenReturn(dto);

        TransactionLinkSuggestionDTO result = service.updateLinkState(99L, TransactionLinkState.UNDECIDED);

        assertEquals(dto, result);
        verify(repository, never()).save(any());
    }

    @Test
    void testUpdateAllForEmptyLists() {
        List<TransactionLinkSuggestionDTO> result = service.updateAllFor(null, null);

        assertTrue(result.isEmpty());
        verifyNoInteractions(repository);
    }
}
