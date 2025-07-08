package com.lennartmoeller.finance.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.TransactionDTO;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.CategoryRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionMapperTest {

    private final TransactionMapper mapper = new TransactionMapperImpl();

    @Mock
    private AccountRepository accRepo;

    @Mock
    private CategoryRepository catRepo;

    private static Account account() {
        Account a = new Account();
        a.setId(1L);
        return a;
    }

    private static Category category() {
        Category c = new Category();
        c.setId(2L);
        return c;
    }

    private static Transaction transaction() {
        Transaction t = new Transaction();
        t.setId(3L);
        t.setAccount(account());
        t.setCategory(category());
        t.setDate(LocalDate.of(2024, 1, 1));
        t.setAmount(50L);
        t.setDescription("desc");
        t.setPinned(true);
        return t;
    }

    @Nested
    class ToDto {
        @Test
        void mapsFields() {
            Transaction tx = transaction();

            TransactionDTO dto = mapper.toDto(tx);

            assertThat(dto.getId()).isEqualTo(tx.getId());
            assertThat(dto.getAccountId()).isEqualTo(tx.getAccount().getId());
            assertThat(dto.getCategoryId()).isEqualTo(tx.getCategory().getId());
            assertThat(dto.getPinned()).isTrue();
        }

        @ParameterizedTest
        @NullSource
        void returnsNullOnNullInput(Transaction tx) {
            assertThat(mapper.toDto(tx)).isNull();
        }

        @Test
        void handlesMissingReferences() {
            Transaction tx = new Transaction();

            TransactionDTO dto = mapper.toDto(tx);

            assertThat(dto.getAccountId()).isNull();
            assertThat(dto.getCategoryId()).isNull();
        }
    }

    @Nested
    class ToEntity {
        @Test
        void resolvesReferencesUsingRepositories() {
            Account acc = account();
            when(accRepo.findById(1L)).thenReturn(Optional.of(acc));
            Category cat = category();
            when(catRepo.findById(2L)).thenReturn(Optional.of(cat));

            TransactionDTO dto = new TransactionDTO();
            dto.setId(3L);
            dto.setAccountId(1L);
            dto.setCategoryId(2L);
            dto.setDate(LocalDate.of(2024, 2, 2));
            dto.setAmount(100L);
            dto.setDescription("desc");
            dto.setPinned(false);

            Transaction entity = mapper.toEntity(dto, accRepo, catRepo);

            assertThat(entity.getAccount()).isSameAs(acc);
            assertThat(entity.getCategory()).isSameAs(cat);
            assertThat(entity.getAmount()).isEqualTo(dto.getAmount());
            verify(accRepo).findById(1L);
            verify(catRepo).findById(2L);
        }

        @Test
        void missingEntitiesResultInNulls() {
            when(accRepo.findById(5L)).thenReturn(Optional.empty());
            when(catRepo.findById(6L)).thenReturn(Optional.empty());

            TransactionDTO dto = new TransactionDTO();
            dto.setAccountId(5L);
            dto.setCategoryId(6L);

            Transaction entity = mapper.toEntity(dto, accRepo, catRepo);

            assertThat(entity.getAccount()).isNull();
            assertThat(entity.getCategory()).isNull();
            verify(accRepo).findById(5L);
            verify(catRepo).findById(6L);
        }

        @ParameterizedTest
        @NullSource
        void returnsNullWhenDtoIsNull(TransactionDTO dto) {
            assertThat(mapper.toEntity(dto, accRepo, catRepo)).isNull();
            verifyNoInteractions(accRepo, catRepo);
        }
    }

    @Nested
    class MappingHelpers {
        @Test
        void helperMethodsFetchFromRepositories() {
            Account acc = account();
            when(accRepo.findById(1L)).thenReturn(Optional.of(acc));
            Category cat = category();
            when(catRepo.findById(2L)).thenReturn(Optional.of(cat));

            assertThat(mapper.mapAccountIdToAccount(1L, accRepo)).isSameAs(acc);
            assertThat(mapper.mapAccountIdToAccount(null, accRepo)).isNull();
            assertThat(mapper.mapAccountToAccountId(acc)).isEqualTo(1L);
            assertThat(mapper.mapAccountToAccountId(null)).isNull();

            assertThat(mapper.mapCategoryIdToCategory(2L, catRepo)).isSameAs(cat);
            assertThat(mapper.mapCategoryIdToCategory(null, catRepo)).isNull();
            assertThat(mapper.mapCategoryToCategoryId(cat)).isEqualTo(2L);
            assertThat(mapper.mapCategoryToCategoryId(null)).isNull();

            when(accRepo.findById(9L)).thenReturn(Optional.empty());
            when(catRepo.findById(10L)).thenReturn(Optional.empty());
            assertThat(mapper.mapAccountIdToAccount(9L, accRepo)).isNull();
            assertThat(mapper.mapCategoryIdToCategory(10L, catRepo)).isNull();
        }
    }
}
