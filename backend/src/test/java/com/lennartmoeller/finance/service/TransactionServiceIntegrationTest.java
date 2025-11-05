package com.lennartmoeller.finance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.lennartmoeller.finance.dto.TransactionDTO;
import com.lennartmoeller.finance.mapper.TargetMapperImpl;
import com.lennartmoeller.finance.mapper.TransactionMapperImpl;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.model.TransactionType;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.CategoryRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import java.time.LocalDate;
import java.util.List;
import javax.annotation.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@DataJpaTest(
        properties = {
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.properties.hibernate.hbm2ddl.import_files="
        })
@Import(TransactionServiceIntegrationTest.Config.class)
class TransactionServiceIntegrationTest {

    @Autowired
    private TransactionService service;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionLinkSuggestionService suggestionService;

    private Account account;
    private Category parent;
    private Category child;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setLabel("A");
        account.setStartBalance(0L);
        account = accountRepository.save(account);

        parent = new Category();
        parent.setLabel("P");
        parent.setTransactionType(TransactionType.EXPENSE);
        parent = categoryRepository.save(parent);

        child = new Category();
        child.setLabel("C");
        child.setTransactionType(TransactionType.EXPENSE);
        child.setParent(parent);
        child = categoryRepository.save(child);
    }

    @Test
    void savePersistsTransactionAndCallsSuggestionService() {
        TransactionDTO dto = new TransactionDTO();
        dto.setAccountId(account.getId());
        dto.setCategoryId(parent.getId());
        dto.setDate(LocalDate.of(2024, 2, 1));
        dto.setAmount(123L);
        dto.setDescription("desc");
        dto.setPinned(true);

        TransactionDTO saved = service.save(dto);

        assertThat(saved.getId()).isNotNull();
        assertThat(transactionRepository.findById(saved.getId())).isPresent();
        verify(suggestionService).updateAllFor(null, List.of(entityManager.find(Transaction.class, saved.getId())));
    }

    @Test
    void deleteByIdRemovesTransaction() {
        Transaction tx = saveTx(account, parent, LocalDate.now(), true);

        service.deleteById(tx.getId());
        assertThat(transactionRepository.findById(tx.getId())).isEmpty();
        verify(suggestionService).removeForTransaction(tx.getId());
    }

    private Transaction saveTx(Account a, Category c, LocalDate date, @Nullable Boolean pinned) {
        Transaction t = new Transaction();
        t.setAccount(a);
        t.setCategory(c);
        t.setDate(date);
        t.setAmount(1L);
        t.setDescription("d");
        t.setPinned(pinned != null ? pinned : false);
        return transactionRepository.save(t);
    }

    @TestConfiguration
    static class Config {
        @Bean
        TransactionService transactionService(
                AccountRepository accountRepository,
                CategoryRepository categoryRepository,
                TransactionLinkSuggestionService suggestionService,
                TransactionRepository transactionRepository) {
            return new TransactionService(
                    accountRepository,
                    categoryRepository,
                    suggestionService,
                    new TransactionMapperImpl(),
                    transactionRepository);
        }

        @Bean
        @Primary
        TransactionLinkSuggestionService suggestionService() {
            return Mockito.mock(TransactionLinkSuggestionService.class);
        }

        @Bean
        TargetMapperImpl targetMapper() {
            return new TargetMapperImpl();
        }
    }
}
