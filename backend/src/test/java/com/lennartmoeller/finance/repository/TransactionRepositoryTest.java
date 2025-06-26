package com.lennartmoeller.finance.repository;

import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.model.TransactionType;
import com.lennartmoeller.finance.projection.DailyBalanceProjection;
import com.lennartmoeller.finance.projection.MonthlyDepositsProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.hbm2ddl.import_files="
})
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private Account depositAcc;
    private Account regularAcc;
    private Category cat1;
    private Category cat2;

    @BeforeEach
    void setUp() {
        depositAcc = new Account();
        depositAcc.setLabel("D");
        depositAcc.setStartBalance(0L);
        depositAcc.setDeposits(true);
        depositAcc = accountRepository.save(depositAcc);

        regularAcc = new Account();
        regularAcc.setLabel("R");
        regularAcc.setStartBalance(0L);
        regularAcc = accountRepository.save(regularAcc);

        cat1 = new Category();
        cat1.setLabel("C1");
        cat1.setTransactionType(TransactionType.INCOME);
        cat1 = categoryRepository.save(cat1);

        cat2 = new Category();
        cat2.setLabel("C2");
        cat2.setTransactionType(TransactionType.EXPENSE);
        cat2 = categoryRepository.save(cat2);

        saveTx(depositAcc, cat1, LocalDate.of(2021,1,10), 100L);
        saveTx(depositAcc, cat1, LocalDate.of(2021,1,11), 50L);
        saveTx(depositAcc, cat2, LocalDate.of(2021,2,15), 200L);
        saveTx(regularAcc, cat1, LocalDate.of(2021,2,15), 300L);
        saveTx(regularAcc, cat2, LocalDate.of(2021,1,10), 10L);
    }

    private void saveTx(Account a, Category c, LocalDate d, long amount) {
        Transaction t = new Transaction();
        t.setAccount(a);
        t.setCategory(c);
        t.setDate(d);
        t.setAmount(amount);
        transactionRepository.save(t);
    }

    @Test
    void testFindFiltered() {
        List<String> jan = List.of("2021-01");
        List<Transaction> result = transactionRepository.findFiltered(
                List.of(depositAcc.getId()), List.of(cat1.getId()), jan);
        assertEquals(2, result.size());
        for (Transaction t : result) {
            assertEquals(depositAcc.getId(), t.getAccount().getId());
            assertEquals(cat1.getId(), t.getCategory().getId());
            assertEquals(2021, t.getDate().getYear());
            assertEquals(1, t.getDate().getMonthValue());
        }

        List<Transaction> all = transactionRepository.findFiltered(null, null, null);
        assertEquals(5, all.size());
    }

    @Test
    void testGetDailyBalances() {
        List<DailyBalanceProjection> balances = transactionRepository.getDailyBalances();
        assertEquals(5, balances.size());

        DailyBalanceProjection first = balances.get(0);
        assertEquals(LocalDate.of(2021,1,10), first.getDate());
        assertEquals(cat1.getId(), first.getCategory().getId());
        assertEquals(100L, first.getBalance());
    }

    @Test
    void testGetMonthlyDeposits() {
        List<MonthlyDepositsProjection> deposits = transactionRepository.getMonthlyDeposits();
        assertEquals(2, deposits.size());
        var map = deposits.stream().collect(Collectors.toMap(MonthlyDepositsProjection::getYearMonth, MonthlyDepositsProjection::getDeposits));
        assertEquals(150L, map.get("2021-01"));
        assertEquals(200L, map.get("2021-02"));
    }
}
