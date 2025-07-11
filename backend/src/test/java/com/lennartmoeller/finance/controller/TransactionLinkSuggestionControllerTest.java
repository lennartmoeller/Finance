package com.lennartmoeller.finance.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.TransactionLinkSuggestionDTO;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import com.lennartmoeller.finance.service.TransactionLinkSuggestionService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionLinkSuggestionControllerTest {
    @Mock
    private TransactionLinkSuggestionService service;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BankTransactionRepository bankTransactionRepository;

    @InjectMocks
    private TransactionLinkSuggestionController controller;

    @Test
    void shouldReturnSuggestions() {
        List<TransactionLinkSuggestionDTO> list = List.of(new TransactionLinkSuggestionDTO());
        when(service.findAll()).thenReturn(list);

        List<TransactionLinkSuggestionDTO> result = controller.getTransactionLinkSuggestions();

        assertThat(result).isEqualTo(list);
        verify(service).findAll();
    }

    @Test
    void shouldGenerateSuggestionsWithoutIds() {
        List<TransactionLinkSuggestionDTO> list = List.of(new TransactionLinkSuggestionDTO());
        when(service.updateAllFor(null, null)).thenReturn(list);

        List<TransactionLinkSuggestionDTO> result = controller.generateTransactionLinkSuggestions(null, null);

        assertThat(result).isEqualTo(list);
        verify(service).updateAllFor(null, null);
    }

    @Test
    void shouldGenerateSuggestionsWithTransactionIdsOnly() {
        List<Long> ids = List.of(1L, 2L);
        List<Transaction> transactions = List.of(new Transaction());
        when(transactionRepository.findAllById(ids)).thenReturn(transactions);
        List<TransactionLinkSuggestionDTO> list = List.of(new TransactionLinkSuggestionDTO());
        when(service.updateAllFor(null, transactions)).thenReturn(list);

        List<TransactionLinkSuggestionDTO> result = controller.generateTransactionLinkSuggestions(ids, null);

        assertThat(result).isEqualTo(list);
        verify(transactionRepository).findAllById(ids);
        verify(service).updateAllFor(null, transactions);
    }

    @Test
    void shouldGenerateSuggestionsWithBankTransactionIdsOnly() {
        List<Long> ids = List.of(3L);
        List<BankTransaction> bankTransactions = List.of(new BankTransaction());
        when(bankTransactionRepository.findAllById(ids)).thenReturn(bankTransactions);
        List<TransactionLinkSuggestionDTO> list = List.of(new TransactionLinkSuggestionDTO());
        when(service.updateAllFor(bankTransactions, null)).thenReturn(list);

        List<TransactionLinkSuggestionDTO> result = controller.generateTransactionLinkSuggestions(null, ids);

        assertThat(result).isEqualTo(list);
        verify(bankTransactionRepository).findAllById(ids);
        verify(service).updateAllFor(bankTransactions, null);
    }

    @Test
    void shouldGenerateSuggestionsWithIds() {
        List<Long> tIds = List.of(1L, 2L);
        List<Long> bIds = List.of(3L);
        List<Transaction> transactions = List.of(new Transaction());
        List<BankTransaction> bankTransactions = List.of(new BankTransaction());
        when(transactionRepository.findAllById(tIds)).thenReturn(transactions);
        when(bankTransactionRepository.findAllById(bIds)).thenReturn(bankTransactions);
        List<TransactionLinkSuggestionDTO> list = List.of(new TransactionLinkSuggestionDTO());
        when(service.updateAllFor(bankTransactions, transactions)).thenReturn(list);

        List<TransactionLinkSuggestionDTO> result = controller.generateTransactionLinkSuggestions(tIds, bIds);

        assertThat(result).isEqualTo(list);
        verify(transactionRepository).findAllById(tIds);
        verify(bankTransactionRepository).findAllById(bIds);
        verify(service).updateAllFor(bankTransactions, transactions);
    }

    @Test
    void shouldReturnSuggestionById() {
        TransactionLinkSuggestionDTO dto = new TransactionLinkSuggestionDTO();
        when(service.findById(5L)).thenReturn(java.util.Optional.of(dto));

        var response = controller.getTransactionLinkSuggestionById(5L);

        assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(dto);
    }

    @Test
    void shouldReturnNotFoundForUnknownSuggestionId() {
        when(service.findById(6L)).thenReturn(java.util.Optional.empty());

        var response = controller.getTransactionLinkSuggestionById(6L);

        assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void shouldUpdateLinkState() {
        TransactionLinkSuggestionDTO dto = new TransactionLinkSuggestionDTO();
        when(service.updateLinkState(7L, com.lennartmoeller.finance.model.TransactionLinkState.CONFIRMED))
                .thenReturn(dto);

        TransactionLinkSuggestionDTO result =
                controller.updateLinkState(7L, com.lennartmoeller.finance.model.TransactionLinkState.CONFIRMED);

        assertThat(result).isSameAs(dto);
    }
}
