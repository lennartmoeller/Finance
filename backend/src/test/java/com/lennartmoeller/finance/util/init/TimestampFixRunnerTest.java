package com.lennartmoeller.finance.util.init;

import static org.junit.jupiter.api.Assertions.*;

import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import com.lennartmoeller.finance.repository.CategoryRepository;
import com.lennartmoeller.finance.repository.InflationRateRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestConstructor;

@DataJpaTest(
        properties = {
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.properties.hibernate.hbm2ddl.import_files="
        })
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class TimestampFixRunnerTest {

    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final InflationRateRepository inflationRateRepository;
    private final TestEntityManager entityManager;

    TimestampFixRunnerTest(
            AccountRepository accountRepository,
            CategoryRepository categoryRepository,
            TransactionRepository transactionRepository,
            BankTransactionRepository bankTransactionRepository,
            InflationRateRepository inflationRateRepository,
            TestEntityManager entityManager) {
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.bankTransactionRepository = bankTransactionRepository;
        this.inflationRateRepository = inflationRateRepository;
        this.entityManager = entityManager;
    }

    @Test
    void testRunnerUpdatesEpochTimestamps() throws Exception {
        entityManager
                .getEntityManager()
                .createNativeQuery(
                        "INSERT INTO accounts (label, start_balance, active, deposits, created_at, modified_at) "
                                + "VALUES ('A', 0, TRUE, FALSE, '1970-01-01 00:00:00', '1970-01-01 00:00:00')")
                .executeUpdate();
        entityManager.flush();

        TimestampFixRunner runner = new TimestampFixRunner(
                accountRepository,
                categoryRepository,
                transactionRepository,
                bankTransactionRepository,
                inflationRateRepository);
        runner.run(null);

        Account saved = accountRepository.findAll().getFirst();
        assertNotEquals(Instant.EPOCH, saved.getCreatedAt().toInstant());
        assertNotEquals(Instant.EPOCH, saved.getModifiedAt().toInstant());
    }
}
