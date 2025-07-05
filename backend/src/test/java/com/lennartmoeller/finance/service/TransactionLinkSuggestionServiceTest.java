package com.lennartmoeller.finance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.lennartmoeller.finance.dto.TransactionLinkSuggestionDTO;
import com.lennartmoeller.finance.mapper.TransactionLinkSuggestionMapper;
import com.lennartmoeller.finance.model.TransactionLinkSuggestion;
import com.lennartmoeller.finance.repository.TransactionLinkSuggestionRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionLinkSuggestionServiceTest {
    private TransactionLinkSuggestionRepository repository;
    private TransactionLinkSuggestionMapper mapper;
    private TransactionLinkSuggestionService service;

    @BeforeEach
    void setUp() {
        repository = mock(TransactionLinkSuggestionRepository.class);
        mapper = mock(TransactionLinkSuggestionMapper.class);
        service = new TransactionLinkSuggestionService(repository, mapper);
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
}
