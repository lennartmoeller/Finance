package com.lennartmoeller.finance.projection;

import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.TransactionType;
import com.lennartmoeller.finance.model.CategorySmoothType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DailyBalanceProjectionTest {

    @Test
    void testSimpleImplementation() {
        Category category = new Category();
        category.setLabel("Test");
        category.setTransactionType(TransactionType.INCOME);
        category.setSmoothType(CategorySmoothType.DAILY);
        LocalDate date = LocalDate.of(2024, 1, 1);

        DailyBalanceProjection p = new SimpleProjection(date, category, 100L);
        assertEquals(date, p.getDate());
        assertEquals(category, p.getCategory());
        assertEquals(100L, p.getBalance());
    }

    private static class SimpleProjection implements DailyBalanceProjection {
        private final LocalDate date;
        private final Category category;
        private final Long balance;
        SimpleProjection(LocalDate date, Category category, Long balance) {
            this.date = date;
            this.category = category;
            this.balance = balance;
        }
        @Override
        public LocalDate getDate() { return date; }
        @Override
        public Category getCategory() { return category; }
        @Override
        public Long getBalance() { return balance; }
    }
}
