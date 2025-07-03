package com.lennartmoeller.finance.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.model.TransactionType;
import com.lennartmoeller.finance.projection.AccountBalanceProjection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(
        properties = {
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.properties.hibernate.hbm2ddl.import_files="
        })
class AccountRepositoryTest {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testGetAccountBalancesAndSummedStartBalance() {
        Account a1 = new Account();
        a1.setLabel("A1");
        a1.setStartBalance(100L);
        accountRepository.save(a1);

        Account a2 = new Account();
        a2.setLabel("A2");
        a2.setStartBalance(200L);
        accountRepository.save(a2);

        Category cat = new Category();
        cat.setLabel("C1");
        cat.setTransactionType(TransactionType.EXPENSE);
        categoryRepository.save(cat);

        Transaction t1 = new Transaction();
        t1.setAccount(a1);
        t1.setCategory(cat);
        t1.setAmount(50L);
        transactionRepository.save(t1);

        Transaction t2 = new Transaction();
        t2.setAccount(a1);
        t2.setCategory(cat);
        t2.setAmount(-10L);
        transactionRepository.save(t2);

        Transaction t3 = new Transaction();
        t3.setAccount(a2);
        t3.setCategory(cat);
        t3.setAmount(20L);
        transactionRepository.save(t3);

        List<AccountBalanceProjection> balances = accountRepository.getAccountBalances();

        assertEquals(2, balances.size());

        AccountBalanceProjection b1 = balances.getFirst();
        assertEquals(a1.getId(), b1.getAccountId());
        assertEquals(140L, b1.getBalance());
        assertEquals(2L, b1.getTransactionCount());

        AccountBalanceProjection b2 = balances.get(1);
        assertEquals(a2.getId(), b2.getAccountId());
        assertEquals(220L, b2.getBalance());
        assertEquals(1L, b2.getTransactionCount());

        Long summed = accountRepository.getSummedStartBalance();
        assertEquals(300L, summed);
    }
}
