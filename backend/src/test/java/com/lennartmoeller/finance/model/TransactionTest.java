package com.lennartmoeller.finance.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class TransactionTest {
    @Test
    void testDefaultValues() {
        Transaction tx = new Transaction();
        assertNotNull(tx.getDate());
        assertEquals(LocalDate.now(), tx.getDate());
        assertEquals("", tx.getDescription());
    }

    @Test
    void testGettersAndSetters() {
        Transaction tx = new Transaction();
        Account account = new Account();
        Category category = new Category();
        tx.setAccount(account);
        tx.setCategory(category);
        tx.setDate(LocalDate.of(2024, 2, 2));
        tx.setAmount(200L);
        tx.setDescription("desc");
        tx.setPinned(true);

        assertEquals(account, tx.getAccount());
        assertEquals(category, tx.getCategory());
        assertEquals(LocalDate.of(2024, 2, 2), tx.getDate());
        assertEquals(200L, tx.getAmount());
        assertEquals("desc", tx.getDescription());
        assertTrue(tx.getPinned());
    }

    @Test
    void testEqualsAndHashCode() {
        Transaction t1 = new Transaction();
        t1.setId(1L);

        Transaction t2 = new Transaction();
        t2.setId(1L);

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());

        t2.setId(2L);
        assertNotEquals(t1, t2);
    }
}
