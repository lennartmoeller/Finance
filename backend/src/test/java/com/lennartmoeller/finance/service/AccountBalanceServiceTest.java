package com.lennartmoeller.finance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.AccountBalanceDTO;
import com.lennartmoeller.finance.projection.AccountBalanceProjection;
import com.lennartmoeller.finance.repository.AccountRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccountBalanceServiceTest {

    private AccountRepository accountRepository;
    private AccountBalanceService service;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        service = new AccountBalanceService(accountRepository);
    }

    @Test
    void testFindAllSortedAndMapped() {
        AccountBalanceProjection p1 = new SimpleProjection(1L, 100L, 5L);
        AccountBalanceProjection p2 = new SimpleProjection(2L, 200L, 10L);
        AccountBalanceProjection p3 = new SimpleProjection(3L, 50L, 1L);
        when(accountRepository.getAccountBalances()).thenReturn(List.of(p1, p2, p3));

        List<AccountBalanceDTO> result = service.findAll();

        // should be sorted descending by transactionCount
        assertEquals(3, result.size());
        assertEquals(2L, result.get(0).getAccountId());
        assertEquals(200L, result.get(0).getBalance());
        assertEquals(1L, result.get(1).getAccountId());
        assertEquals(100L, result.get(1).getBalance());
        assertEquals(3L, result.get(2).getAccountId());
        assertEquals(50L, result.get(2).getBalance());
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
