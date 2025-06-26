package com.lennartmoeller.finance.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

        @Test
        void testDefaultSmoothType() {
                Category category = new Category();
                assertEquals(CategorySmoothType.DAILY, category.getSmoothType());
        }

        @Test
        void testGettersAndSetters() {
                Category category = new Category();
                category.setLabel("Food");
                category.setTransactionType(TransactionType.EXPENSE);
                category.setSmoothType(CategorySmoothType.MONTHLY);
                category.setIcon("icon.png");

                assertEquals("Food", category.getLabel());
                assertEquals(TransactionType.EXPENSE, category.getTransactionType());
                assertEquals(CategorySmoothType.MONTHLY, category.getSmoothType());
                assertEquals("icon.png", category.getIcon());
        }

        @Test
        void testEqualsAndHashCode() {
                Category c1 = new Category();
                c1.setId(1L);
                c1.setLabel("A");

                Category c2 = new Category();
                c2.setId(1L);
                c2.setLabel("B");

                assertEquals(c1, c2);
                assertEquals(c1.hashCode(), c2.hashCode());

                c2.setId(2L);
                assertNotEquals(c1, c2);
        }
}
