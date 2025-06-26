package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.TransactionDTO;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.CategoryRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionMapperTest {

    private static void inject(Object target, Class<?> owner, String fieldName, Object value) throws Exception {
        Field f = owner.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

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
        tx.setDate(LocalDate.of(2024,1,1));
        tx.setAmount(500L);
        tx.setDescription("Desc");

        TransactionMapper mapper = new TransactionMapperImpl();
        TransactionDTO dto = mapper.toDto(tx);

        assertEquals(tx.getId(), dto.getId());
        assertEquals(account.getId(), dto.getAccountId());
        assertEquals(category.getId(), dto.getCategoryId());
        assertEquals(tx.getDate(), dto.getDate());
        assertEquals(tx.getAmount(), dto.getAmount());
        assertEquals(tx.getDescription(), dto.getDescription());
    }

    @Test
    void testToEntityUsesRepositories() throws Exception {
        AccountRepository accRepo = mock(AccountRepository.class);
        CategoryRepository catRepo = mock(CategoryRepository.class);

        Account account = new Account();
        account.setId(2L);
        when(accRepo.findById(2L)).thenReturn(Optional.of(account));

        Category category = new Category();
        category.setId(3L);
        when(catRepo.findById(3L)).thenReturn(Optional.of(category));

        TransactionMapperImpl mapper = new TransactionMapperImpl();
        inject(mapper, TransactionMapper.class, "accountRepository", accRepo);
        inject(mapper, TransactionMapper.class, "categoryRepository", catRepo);

        TransactionDTO dto = new TransactionDTO();
        dto.setId(4L);
        dto.setAccountId(2L);
        dto.setCategoryId(3L);
        dto.setDate(LocalDate.of(2024,2,2));
        dto.setAmount(600L);
        dto.setDescription("Desc");

        Transaction entity = mapper.toEntity(dto);

        assertEquals(dto.getId(), entity.getId());
        assertSame(account, entity.getAccount());
        assertSame(category, entity.getCategory());
        assertEquals(dto.getDate(), entity.getDate());
        assertEquals(dto.getAmount(), entity.getAmount());
        assertEquals(dto.getDescription(), entity.getDescription());
        verify(accRepo).findById(2L);
        verify(catRepo).findById(3L);
    }
}
