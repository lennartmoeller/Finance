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
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
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
        Account account = account(10L);
        BankTransaction bank = bankTransaction(1L, account, 100L, LocalDate.of(2024, 1, 2));
        Transaction t1 = transaction(2L, account, 100L, LocalDate.of(2024, 1, 2));
        Transaction t2 = transaction(3L, account, 100L, LocalDate.of(2024, 1, 5));

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
        Account account = account(20L);
        BankTransaction bank = bankTransaction(21L, account, 50L, LocalDate.of(2024, 2, 2));
        Transaction transaction = transaction(22L, account, 50L, LocalDate.of(2024, 2, 3));

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
        Transaction transaction = transaction(40L, null, 0L, LocalDate.now());
        BankTransaction bt = bankTransaction(41L, null, 0L, LocalDate.now());
        TransactionLinkSuggestion undecided = suggestion(TransactionLinkState.UNDECIDED, bt, transaction);
        TransactionLinkSuggestion confirmed = suggestion(TransactionLinkState.CONFIRMED, bt, transaction);

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
        Account account = account(1L);
        BankTransaction bankTransaction = bankTransaction(50L, account, 1L, LocalDate.now());

        TransactionLinkSuggestion auto =
                suggestion(TransactionLinkState.AUTO_CONFIRMED, bankTransaction, new Transaction());
        TransactionLinkSuggestion rejected =
                suggestion(TransactionLinkState.REJECTED, bankTransaction, new Transaction());

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
        Account account = account(1L);
        Transaction t = transaction(1L, account, 5L, LocalDate.now());
        BankTransaction b = bankTransaction(2L, account, 5L, LocalDate.now());
        when(repository.findAllByBankTransactionIdsOrTransactionIds(List.of(2L), List.of(1L)))
                .thenReturn(new java.util.ArrayList<>());
        when(bankTransactionRepository.findAll()).thenReturn(List.of(b));
        when(transactionRepository.findAll()).thenReturn(List.of(t));
        when(repository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toDto(any())).thenReturn(new TransactionLinkSuggestionDTO());

        List<TransactionLinkSuggestionDTO> result = service.updateAllFor(List.of(b), List.of(t));

        assertEquals(1, result.size());
    }

    @ParameterizedTest
    @MethodSource("emptyUpdateInputs")
    void testUpdateAllForNoInput(List<BankTransaction> bankTransactions, List<Transaction> transactions) {
        service.updateAllFor(bankTransactions, transactions);
        verifyNoInteractions(repository);
    }

    private static Stream<org.junit.jupiter.params.provider.Arguments> emptyUpdateInputs() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(null, null),
                org.junit.jupiter.params.provider.Arguments.of(null, List.of()),
                org.junit.jupiter.params.provider.Arguments.of(List.of(), null),
                org.junit.jupiter.params.provider.Arguments.of(List.of(), List.of()));
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
        TransactionLinkSuggestion suggestion = suggestion(
                TransactionLinkState.UNDECIDED,
                bankTransaction(1L, null, 0L, LocalDate.now()),
                transaction(1L, null, 0L, LocalDate.now()));
        TransactionLinkSuggestion saved = suggestion(
                TransactionLinkState.UNDECIDED,
                bankTransaction(1L, null, 0L, LocalDate.now()),
                transaction(1L, null, 0L, LocalDate.now()));
        TransactionLinkSuggestionDTO dto = new TransactionLinkSuggestionDTO();
        when(repository.findById(12L)).thenReturn(java.util.Optional.of(suggestion));
        when(repository.save(suggestion)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(dto);

        TransactionLinkSuggestionDTO result = service.updateLinkState(12L, TransactionLinkState.CONFIRMED);

        assertEquals(dto, result);
        assertEquals(TransactionLinkState.CONFIRMED, suggestion.getLinkState());
    }

    @ParameterizedTest
    @EnumSource(
            value = TransactionLinkState.class,
            names = {"AUTO_CONFIRMED", "AUTO_REJECTED"})
    void testUpdateLinkStateRejectsAutoStates(TransactionLinkState state) {
        assertThrows(IllegalArgumentException.class, () -> service.updateLinkState(1L, state));
    }

    @Test
    void updateLinkStateRejectsOtherSuggestionsWhenOneConfirmedExists() {
        BankTransaction bankTransaction = bankTransaction(1L, null, 0L, LocalDate.now());
        Transaction t1 = transaction(1L, null, 0L, LocalDate.now());
        Transaction t2 = transaction(2L, null, 0L, LocalDate.now());

        TransactionLinkSuggestion toUpdate = suggestion(TransactionLinkState.UNDECIDED, bankTransaction, t1);
        toUpdate.setId(10L);

        TransactionLinkSuggestion alreadyConfirmed = suggestion(TransactionLinkState.CONFIRMED, bankTransaction, t2);
        alreadyConfirmed.setId(11L);

        TransactionLinkSuggestion undecided = suggestion(TransactionLinkState.UNDECIDED, bankTransaction, t2);
        undecided.setId(12L);

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
        BankTransaction bankTransaction = bankTransaction(2L, null, 0L, LocalDate.now());
        Transaction t1 = transaction(3L, null, 0L, LocalDate.now());
        Transaction t2 = transaction(4L, null, 0L, LocalDate.now());

        TransactionLinkSuggestion toUpdate = suggestion(TransactionLinkState.CONFIRMED, bankTransaction, t1);
        toUpdate.setId(20L);

        TransactionLinkSuggestion auto = suggestion(TransactionLinkState.AUTO_CONFIRMED, bankTransaction, t2);
        auto.setId(21L);

        TransactionLinkSuggestion undecided = suggestion(TransactionLinkState.UNDECIDED, bankTransaction, t2);
        undecided.setId(22L);

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

    private static Account account(long id) {
        Account a = new Account();
        a.setId(id);
        a.setIban("DE" + id);
        return a;
    }

    private static BankTransaction bankTransaction(long id, Account account, long amount, LocalDate date) {
        BankTransaction bt = new BankTransaction();
        bt.setId(id);
        bt.setAccount(account);
        bt.setAmount(amount);
        bt.setBookingDate(date);
        return bt;
    }

    private static Transaction transaction(long id, Account account, long amount, LocalDate date) {
        Transaction t = new Transaction();
        t.setId(id);
        t.setAccount(account);
        t.setAmount(amount);
        t.setDate(date);
        return t;
    }

    private static TransactionLinkSuggestion suggestion(TransactionLinkState state, BankTransaction bt, Transaction t) {
        TransactionLinkSuggestion s = new TransactionLinkSuggestion();
        s.setLinkState(state);
        s.setBankTransaction(bt);
        s.setTransaction(t);
        return s;
    }
}
