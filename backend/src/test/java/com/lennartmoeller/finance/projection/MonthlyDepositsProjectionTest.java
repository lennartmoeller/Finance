package com.lennartmoeller.finance.projection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MonthlyDepositsProjectionTest {

    @Test
    void testSimpleImplementation() {
        MonthlyDepositsProjection p = new SimpleProjection("2024-05", 1500L);
        assertEquals("2024-05", p.getYearMonth());
        assertEquals(1500L, p.getDeposits());
    }

    private static class SimpleProjection implements MonthlyDepositsProjection {
        private final String yearMonth;
        private final Long deposits;

        SimpleProjection(String yearMonth, Long deposits) {
            this.yearMonth = yearMonth;
            this.deposits = deposits;
        }

        @Override
        public String getYearMonth() {
            return yearMonth;
        }

        @Override
        public Long getDeposits() {
            return deposits;
        }
    }
}
