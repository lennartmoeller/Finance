package com.lennartmoeller.finance.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.TransactionLinkSuggestionDTO;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import com.lennartmoeller.finance.service.TransactionLinkSuggestionService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionLinkSuggestionControllerTest {
    private TransactionLinkSuggestionService service;
    private TransactionLinkSuggestionController controller;

    @BeforeEach
    void setUp() {
        service = mock(TransactionLinkSuggestionService.class);
        TransactionRepository transactionRepository = mock(TransactionRepository.class);
        BankTransactionRepository bankTransactionRepository = mock(BankTransactionRepository.class);
        controller = new TransactionLinkSuggestionController(service, transactionRepository, bankTransactionRepository);
    }

    @Test
    void testGetTransactionLinkSuggestions() {
        List<TransactionLinkSuggestionDTO> list =
                List.of(new TransactionLinkSuggestionDTO(), new TransactionLinkSuggestionDTO());
        when(service.findAll()).thenReturn(list);

        List<TransactionLinkSuggestionDTO> result = controller.getTransactionLinkSuggestions();

        assertEquals(list, result);
        verify(service).findAll();
    }

    @Test
    void testGenerateTransactionLinkSuggestions() {
        List<TransactionLinkSuggestionDTO> list = List.of(new TransactionLinkSuggestionDTO());
        when(service.generateSuggestions(null, null)).thenReturn(list);

        List<TransactionLinkSuggestionDTO> result = controller.generateTransactionLinkSuggestions(null, null);

        assertEquals(list, result);
        verify(service).generateSuggestions(null, null);
    }
}
