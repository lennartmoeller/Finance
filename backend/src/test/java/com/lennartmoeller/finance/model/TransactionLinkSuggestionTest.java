package com.lennartmoeller.finance.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TransactionLinkSuggestionTest {
    @Test
    void testGettersAndSetters() {
        TransactionLinkSuggestion suggestion = new TransactionLinkSuggestion();
        BankTransaction btx = new BankTransaction();
        Transaction tx = new Transaction();
        suggestion.setBankTransaction(btx);
        suggestion.setTransaction(tx);
        suggestion.setProbability(0.5);
        suggestion.setLinked(true);

        assertEquals(btx, suggestion.getBankTransaction());
        assertEquals(tx, suggestion.getTransaction());
        assertEquals(0.5, suggestion.getProbability());
        assertTrue(suggestion.getLinked());
    }

    @Test
    void testEqualsAndHashCode() {
        TransactionLinkSuggestion s1 = new TransactionLinkSuggestion();
        s1.setId(1L);
        TransactionLinkSuggestion s2 = new TransactionLinkSuggestion();
        s2.setId(1L);
        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
        s2.setId(2L);
        assertNotEquals(s1, s2);
    }
}
