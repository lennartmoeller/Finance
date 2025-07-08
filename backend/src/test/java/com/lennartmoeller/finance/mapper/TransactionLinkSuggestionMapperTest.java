package com.lennartmoeller.finance.mapper;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionLinkSuggestionMapperTest {

    private final TransactionLinkSuggestionMapper mapper = new TransactionLinkSuggestionMapperImpl();

    @Mock
    private BankTransactionRepository bankRepo;

    @Mock
    private TransactionRepository txRepo;

    private static BankTransaction bankTx() {
        BankTransaction b = new BankTransaction();
        b.setId(1L);
        return b;
    }

    private static Transaction tx() {
        Transaction t = new Transaction();
        t.setId(2L);
        return t;
    }

    @Nested
    class ToDto {
        @Test
        void mapsFields() {
            TransactionLinkSuggestion entity = new TransactionLinkSuggestion();
            entity.setId(3L);
            entity.setBankTransaction(bankTx());
            entity.setTransaction(tx());
            entity.setProbability(0.5);
            entity.setLinkState(TransactionLinkState.CONFIRMED);

            TransactionLinkSuggestionDTO dto = mapper.toDto(entity);

            assertThat(dto.getId()).isEqualTo(3L);
            assertThat(dto.getBankTransactionId()).isEqualTo(1L);
            assertThat(dto.getTransactionId()).isEqualTo(2L);
            assertThat(dto.getProbability()).isEqualTo(0.5);
            assertThat(dto.getLinkState()).isEqualTo(TransactionLinkState.CONFIRMED);
        }

        @ParameterizedTest
        @NullSource
        void returnsNullOnNullInput(TransactionLinkSuggestion entity) {
            assertThat(mapper.toDto(entity)).isNull();
        }
    }

    @Nested
    class ToEntity {
        @Test
        void resolvesReferencesUsingRepositories() {
            BankTransaction b = bankTx();
            when(bankRepo.findById(1L)).thenReturn(Optional.of(b));
            Transaction t = tx();
            when(txRepo.findById(2L)).thenReturn(Optional.of(t));

            TransactionLinkSuggestionDTO dto = new TransactionLinkSuggestionDTO();
            dto.setId(3L);
            dto.setBankTransactionId(1L);
            dto.setTransactionId(2L);
            dto.setProbability(0.7);
            dto.setLinkState(TransactionLinkState.UNDECIDED);

            TransactionLinkSuggestion entity = mapper.toEntity(dto, bankRepo, txRepo);

            assertThat(entity.getBankTransaction()).isSameAs(b);
            assertThat(entity.getTransaction()).isSameAs(t);
            assertThat(entity.getLinkState()).isEqualTo(TransactionLinkState.UNDECIDED);
            verify(bankRepo).findById(1L);
            verify(txRepo).findById(2L);
        }

        @Test
        void missingIdsAreIgnored() {
            TransactionLinkSuggestionDTO dto = new TransactionLinkSuggestionDTO();
            dto.setProbability(1.0);

            TransactionLinkSuggestion entity = mapper.toEntity(dto, bankRepo, txRepo);

            assertThat(entity.getBankTransaction()).isNull();
            assertThat(entity.getTransaction()).isNull();
            verifyNoInteractions(bankRepo, txRepo);
        }

        @ParameterizedTest
        @NullSource
        void returnsNullWhenDtoIsNull(TransactionLinkSuggestionDTO dto) {
            assertThat(mapper.toEntity(dto, bankRepo, txRepo)).isNull();
            verifyNoInteractions(bankRepo, txRepo);
        }
    }

    @Nested
    class MappingHelpers {
        @Test
        void helperMethodsFetchFromRepositories() {
            BankTransaction b = bankTx();
            when(bankRepo.findById(1L)).thenReturn(Optional.of(b));
            Transaction t = tx();
            when(txRepo.findById(2L)).thenReturn(Optional.of(t));

            assertThat(mapper.mapBankTransactionIdToBankTransaction(1L, bankRepo))
                    .isSameAs(b);
            assertThat(mapper.mapBankTransactionIdToBankTransaction(null, bankRepo))
                    .isNull();
            assertThat(mapper.mapTransactionIdToTransaction(2L, txRepo)).isSameAs(t);
            assertThat(mapper.mapTransactionIdToTransaction(null, txRepo)).isNull();
        }
    }
}
