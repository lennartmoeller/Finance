package com.lennartmoeller.finance.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lennartmoeller.finance.repository.AccountRepository;
import java.sql.Timestamp;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestConstructor;

@DataJpaTest(
        properties = {
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.properties.hibernate.hbm2ddl.import_files="
        })
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class BaseModelTest {
    private final AccountRepository accountRepository;

    BaseModelTest(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Test
    void testCreatedAndModifiedAreSet() {
        Account account = new Account();
        account.setLabel("A");
        account.setStartBalance(0L);
        Account saved = accountRepository.saveAndFlush(account);

        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getModifiedAt());
        Timestamp created = saved.getCreatedAt();
        Timestamp modified = saved.getModifiedAt();

        saved.setLabel("B");
        Account updated = accountRepository.saveAndFlush(saved);

        assertEquals(created, updated.getCreatedAt());
        assertTrue(updated.getModifiedAt().after(modified)
                || updated.getModifiedAt().equals(modified));
    }
}
