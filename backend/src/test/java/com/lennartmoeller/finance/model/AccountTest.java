package com.lennartmoeller.finance.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AccountTest {
    @Test
    void testDefaultValues() {
        Account account = new Account();
        assertNull(account.getIban());
        assertTrue(account.getActive());
        assertFalse(account.getDeposits());
    }

    @Test
    void testGettersAndSetters() {
        Account account = new Account();
        account.setLabel("Checking");
        account.setIban("DE123");
        account.setStartBalance(1000L);
        account.setActive(false);
        account.setDeposits(true);

        assertEquals("Checking", account.getLabel());
        assertEquals("DE123", account.getIban());
        assertEquals(1000L, account.getStartBalance());
        assertFalse(account.getActive());
        assertTrue(account.getDeposits());
    }

    @Test
    void testEqualsAndHashCode() {
        Account a1 = new Account();
        a1.setId(1L);
        a1.setLabel("A1");

        Account a2 = new Account();
        a2.setId(1L);
        a2.setLabel("Different");

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());

        a2.setId(2L);
        assertNotEquals(a1, a2);
    }
}
