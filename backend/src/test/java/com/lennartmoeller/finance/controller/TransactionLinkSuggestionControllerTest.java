package com.lennartmoeller.finance.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.lennartmoeller.finance.dto.TransactionLinkSuggestionDTO;
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
        controller = new TransactionLinkSuggestionController(service);
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
}
