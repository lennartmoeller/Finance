package com.lennartmoeller.finance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import com.lennartmoeller.finance.mapper.BankTransactionMapper;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BankTransactionServiceTest {
    private BankTransactionRepository repository;
    private BankTransactionMapper mapper;
    private BankTransactionService service;

    @BeforeEach
    void setUp() {
        repository = mock(BankTransactionRepository.class);
        mapper = mock(BankTransactionMapper.class);
        service = new BankTransactionService(mapper, repository);
    }

    @Test
    void testFindAll() {
        BankTransaction t = new BankTransaction();
        t.setBank(com.lennartmoeller.finance.model.BankType.ING_V1);
        IngV1TransactionDTO dto = new IngV1TransactionDTO();
        when(repository.findAll()).thenReturn(List.of(t));
        when(mapper.toDto(t)).thenReturn(dto);

        List<BankTransactionDTO> result = service.findAll();

        assertEquals(List.of(dto), result);
    }

    @Test
    void testFindByIdFound() {
        BankTransaction t = new BankTransaction();
        t.setBank(com.lennartmoeller.finance.model.BankType.ING_V1);
        IngV1TransactionDTO dto = new IngV1TransactionDTO();
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
}
