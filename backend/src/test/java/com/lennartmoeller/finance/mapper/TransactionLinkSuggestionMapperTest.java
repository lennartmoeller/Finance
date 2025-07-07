package com.lennartmoeller.finance.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.TransactionLinkSuggestionDTO;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.model.TransactionLinkState;
import com.lennartmoeller.finance.model.TransactionLinkSuggestion;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class TransactionLinkSuggestionMapperTest {

    private final TransactionLinkSuggestionMapper mapper = new TransactionLinkSuggestionMapperImpl();

    @Test
    void shouldMapEntityToDto() {
        BankTransaction bankTx = new BankTransaction();
        bankTx.setId(1L);
        Transaction tx = new Transaction();
        tx.setId(2L);

        TransactionLinkSuggestion entity = new TransactionLinkSuggestion();
        entity.setId(3L);
        entity.setBankTransaction(bankTx);
        entity.setTransaction(tx);
        entity.setProbability(0.5);
        entity.setLinkState(TransactionLinkState.CONFIRMED);

        TransactionLinkSuggestionDTO dto = mapper.toDto(entity);

        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getBankTransactionId()).isEqualTo(1L);
        assertThat(dto.getTransactionId()).isEqualTo(2L);
        assertThat(dto.getProbability()).isEqualTo(0.5);
        assertThat(dto.getLinkState()).isEqualTo(TransactionLinkState.CONFIRMED);
    }

    @Test
    void shouldHandleNullNestedEntitiesWhenMappingToDto() {
        TransactionLinkSuggestion entity = new TransactionLinkSuggestion();
        entity.setId(4L);

        TransactionLinkSuggestionDTO dto = mapper.toDto(entity);

        assertThat(dto.getId()).isEqualTo(4L);
        assertThat(dto.getBankTransactionId()).isNull();
        assertThat(dto.getTransactionId()).isNull();
    }

    @Test
    void shouldMapDtoToEntityUsingRepositories() {
        BankTransactionRepository bankRepo = mock(BankTransactionRepository.class);
        TransactionRepository txRepo = mock(TransactionRepository.class);

        BankTransaction bankTx = new BankTransaction();
        bankTx.setId(10L);
        when(bankRepo.findById(10L)).thenReturn(Optional.of(bankTx));
        Transaction tx = new Transaction();
        tx.setId(20L);
        when(txRepo.findById(20L)).thenReturn(Optional.of(tx));

        TransactionLinkSuggestionDTO dto = new TransactionLinkSuggestionDTO();
        dto.setId(5L);
        dto.setBankTransactionId(10L);
        dto.setTransactionId(20L);
        dto.setProbability(0.8);
        dto.setLinkState(TransactionLinkState.UNDECIDED);

        TransactionLinkSuggestion entity = mapper.toEntity(dto, bankRepo, txRepo);

        assertThat(entity.getId()).isEqualTo(5L);
        assertThat(entity.getProbability()).isEqualTo(0.8);
        assertThat(entity.getLinkState()).isEqualTo(TransactionLinkState.UNDECIDED);
        assertThat(entity.getBankTransaction()).isSameAs(bankTx);
        assertThat(entity.getTransaction()).isSameAs(tx);

        verify(bankRepo).findById(10L);
        verify(txRepo).findById(20L);
    }

    @Test
    void shouldIgnoreNullIdsWhenMappingToEntity() {
        BankTransactionRepository bankRepo = mock(BankTransactionRepository.class);
        TransactionRepository txRepo = mock(TransactionRepository.class);

        TransactionLinkSuggestionDTO dto = new TransactionLinkSuggestionDTO();
        dto.setProbability(1.0);

        TransactionLinkSuggestion entity = mapper.toEntity(dto, bankRepo, txRepo);

        assertThat(entity.getBankTransaction()).isNull();
        assertThat(entity.getTransaction()).isNull();
        verifyNoInteractions(bankRepo, txRepo);
    }

    @Test
    void nullInputsReturnNull() {
        assertThat(mapper.toDto(null)).isNull();
        assertThat(mapper.toEntity(null, null, null)).isNull();
    }

    @Test
    void mappingHelpersReturnValuesFromRepositories() {
        BankTransactionRepository bankRepo = mock(BankTransactionRepository.class);
        TransactionRepository txRepo = mock(TransactionRepository.class);
        BankTransaction bankTx = new BankTransaction();
        bankTx.setId(7L);
        when(bankRepo.findById(7L)).thenReturn(Optional.of(bankTx));
        Transaction tx = new Transaction();
        tx.setId(8L);
        when(txRepo.findById(8L)).thenReturn(Optional.of(tx));

        assertThat(mapper.mapBankTransactionIdToBankTransaction(7L, bankRepo)).isSameAs(bankTx);
        assertThat(mapper.mapBankTransactionIdToBankTransaction(null, bankRepo)).isNull();
        assertThat(mapper.mapTransactionIdToTransaction(8L, txRepo)).isSameAs(tx);
        assertThat(mapper.mapTransactionIdToTransaction(null, txRepo)).isNull();
    }
}
