package com.lennartmoeller.finance.projection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AccountBalanceProjectionTest {
    @Test
    void testSimpleImplementation() {
        AccountBalanceProjection p = new SimpleProjection(1L, 200L, 3L);
        assertEquals(1L, p.getAccountId());
        assertEquals(200L, p.getBalance());
        assertEquals(3L, p.getTransactionCount());
    }

    private static class SimpleProjection implements AccountBalanceProjection {
        private final Long accountId;
        private final Long balance;
        private final Long transactionCount;

        SimpleProjection(Long accountId, Long balance, Long transactionCount) {
            this.accountId = accountId;
            this.balance = balance;
            this.transactionCount = transactionCount;
        }

        @Override
        public Long getAccountId() {
            return accountId;
        }

        @Override
        public Long getBalance() {
            return balance;
        }

        @Override
        public Long getTransactionCount() {
            return transactionCount;
        }
    }
}
