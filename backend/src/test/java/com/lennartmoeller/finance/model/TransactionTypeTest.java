package com.lennartmoeller.finance.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TransactionTypeTest {
    @Test
    void testEnumValues() {
        TransactionType[] values = TransactionType.values();
        assertEquals(3, values.length);
        assertEquals(TransactionType.EXPENSE, values[0]);
        assertEquals(TransactionType.INCOME, values[1]);
        assertEquals(TransactionType.INVESTMENT, values[2]);
    }
}
