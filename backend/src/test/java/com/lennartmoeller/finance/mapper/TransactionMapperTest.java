package com.lennartmoeller.finance.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.TransactionDTO;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.CategoryRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class TransactionMapperTest {
    @Test
    void testToDto() {
        Account account = new Account();
        account.setId(2L);
        Category category = new Category();
        category.setId(3L);

        Transaction tx = new Transaction();
        tx.setId(4L);
        tx.setAccount(account);
        tx.setCategory(category);
        tx.setDate(LocalDate.of(2024, 1, 1));
        tx.setAmount(500L);
        tx.setDescription("Desc");
        tx.setPinned(true);

        TransactionMapper mapper = new TransactionMapperImpl();
        TransactionDTO dto = mapper.toDto(tx);

        assertThat(dto.getId()).isEqualTo(tx.getId());
        assertThat(dto.getAccountId()).isEqualTo(account.getId());
        assertThat(dto.getCategoryId()).isEqualTo(category.getId());
        assertThat(dto.getDate()).isEqualTo(tx.getDate());
        assertThat(dto.getAmount()).isEqualTo(tx.getAmount());
        assertThat(dto.getDescription()).isEqualTo(tx.getDescription());
        assertThat(dto.getPinned()).isTrue();
    }

    @Test
    void testToDtoNulls() {
        TransactionMapper mapper = new TransactionMapperImpl();
        assertThat(mapper.toDto(null)).isNull();

        Transaction tx = new Transaction();
        TransactionDTO dto = mapper.toDto(tx);
        assertThat(dto.getAccountId()).isNull();
        assertThat(dto.getCategoryId()).isNull();
        assertThat(dto.getPinned()).isFalse();
    }

    @Test
    void testToEntityUsesRepositories() {
        AccountRepository accRepo = mock(AccountRepository.class);
        CategoryRepository catRepo = mock(CategoryRepository.class);

        Account account = new Account();
        account.setId(2L);
        when(accRepo.findById(2L)).thenReturn(Optional.of(account));

        Category category = new Category();
        category.setId(3L);
        when(catRepo.findById(3L)).thenReturn(Optional.of(category));

        TransactionMapperImpl mapper = new TransactionMapperImpl();

        TransactionDTO dto = new TransactionDTO();
        dto.setId(4L);
        dto.setAccountId(2L);
        dto.setCategoryId(3L);
        dto.setDate(LocalDate.of(2024, 2, 2));
        dto.setAmount(600L);
        dto.setDescription("Desc");

        Transaction entity = mapper.toEntity(dto, accRepo, catRepo);

        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getAccount()).isSameAs(account);
        assertThat(entity.getCategory()).isSameAs(category);
        assertThat(entity.getDate()).isEqualTo(dto.getDate());
        assertThat(entity.getAmount()).isEqualTo(dto.getAmount());
        assertThat(entity.getDescription()).isEqualTo(dto.getDescription());
        verify(accRepo).findById(2L);
        verify(catRepo).findById(3L);
    }

    @Test
    void testToEntityNullsAndMissing() {
        AccountRepository accRepo = mock(AccountRepository.class);
        CategoryRepository catRepo = mock(CategoryRepository.class);
        when(accRepo.findById(1L)).thenReturn(Optional.empty());
        when(catRepo.findById(2L)).thenReturn(Optional.empty());

        TransactionMapperImpl mapper = new TransactionMapperImpl();

        assertThat(mapper.toEntity(null, accRepo, catRepo)).isNull();
        TransactionDTO dto = new TransactionDTO();
        dto.setAccountId(1L);
        dto.setCategoryId(2L);

        Transaction entity = mapper.toEntity(dto, accRepo, catRepo);
        assertThat(entity.getAccount()).isNull();
        assertThat(entity.getCategory()).isNull();
        verify(accRepo).findById(1L);
        verify(catRepo).findById(2L);
    }

    @Test
    void testMappingHelpers() {
        TransactionMapperImpl mapper = new TransactionMapperImpl();
        AccountRepository accRepo = mock(AccountRepository.class);
        CategoryRepository catRepo = mock(CategoryRepository.class);

        Account account = new Account();
        account.setId(11L);
        when(accRepo.findById(11L)).thenReturn(Optional.of(account));
        Category category = new Category();
        category.setId(12L);
        when(catRepo.findById(12L)).thenReturn(Optional.of(category));

        assertThat(mapper.mapAccountIdToAccount(11L, accRepo)).isSameAs(account);
        assertThat(mapper.mapAccountIdToAccount(null, accRepo)).isNull();

        assertThat(mapper.mapAccountToAccountId(account)).isEqualTo(11L);
        assertThat(mapper.mapAccountToAccountId(null)).isNull();

        assertThat(mapper.mapCategoryIdToCategory(12L, catRepo)).isSameAs(category);
        assertThat(mapper.mapCategoryIdToCategory(null, catRepo)).isNull();

        assertThat(mapper.mapCategoryToCategoryId(category)).isEqualTo(12L);
        assertThat(mapper.mapCategoryToCategoryId(null)).isNull();
    }
}
