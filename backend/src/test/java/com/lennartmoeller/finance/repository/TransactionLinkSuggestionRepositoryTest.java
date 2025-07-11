package com.lennartmoeller.finance.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.BankType;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.model.TransactionLinkSuggestion;
import com.lennartmoeller.finance.model.TransactionType;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(
        properties = {
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.properties.hibernate.hbm2ddl.import_files="
        })
class TransactionLinkSuggestionRepositoryTest {
    @Autowired
    private TransactionLinkSuggestionRepository suggestionRepository;

    @Autowired
    private BankTransactionRepository bankTransactionRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private BankTransaction bank1;
    private BankTransaction bank2;
    private Transaction tx1;
    private TransactionLinkSuggestion link1;
    private TransactionLinkSuggestion link2;
    private TransactionLinkSuggestion link3;

    @BeforeEach
    void setUp() {
        Account acc = newAccount();
        Category cat = newCategory();
        bank1 = bankTransactionRepository.save(newBankTransaction(acc, Map.of("b", "1")));
        bank2 = bankTransactionRepository.save(newBankTransaction(acc, Map.of("b", "2")));
        tx1 = transactionRepository.save(newTransaction(acc, cat, LocalDate.now()));
        Transaction tx2 = transactionRepository.save(
                newTransaction(acc, cat, LocalDate.now().plusDays(1)));

        link1 = suggestionRepository.save(newLink(bank1, tx1));
        link2 = suggestionRepository.save(newLink(bank2, tx1));
        link3 = suggestionRepository.save(newLink(bank2, tx2));
    }

    @Test
    void findAllByBankTransactionIdsOrTransactionIdsRespectsFilters() {
        assertThat(suggestionRepository.findAllByBankTransactionIdsOrTransactionIds(null, null))
                .isEmpty();

        assertThat(suggestionRepository.findAllByBankTransactionIdsOrTransactionIds(List.of(bank1.getId()), null))
                .containsExactly(link1);

        assertThat(suggestionRepository.findAllByBankTransactionIdsOrTransactionIds(null, List.of(tx1.getId())))
                .containsExactlyInAnyOrder(link1, link2);

        assertThat(suggestionRepository.findAllByBankTransactionIdsOrTransactionIds(
                        List.of(bank2.getId()), List.of(tx1.getId())))
                .containsExactlyInAnyOrder(link1, link2, link3);
    }

    @Test
    void deleteMethodsRemoveMatchingSuggestions() {
        suggestionRepository.deleteAllByTransaction_Id(tx1.getId());
        assertThat(suggestionRepository.findAll()).containsExactly(link3);
    }

    private Account newAccount() {
        Account a = new Account();
        a.setLabel("acc");
        a.setStartBalance(0L);
        return accountRepository.save(a);
    }

    private Category newCategory() {
        Category c = new Category();
        c.setLabel("cat");
        c.setTransactionType(TransactionType.EXPENSE);
        return categoryRepository.save(c);
    }

    private Transaction newTransaction(Account account, Category category, LocalDate date) {
        Transaction t = new Transaction();
        t.setAccount(account);
        t.setCategory(category);
        t.setDate(date);
        t.setAmount(1L);
        return t;
    }

    private BankTransaction newBankTransaction(Account acc, Map<String, String> data) {
        BankTransaction b = new BankTransaction();
        b.setAccount(acc);
        b.setBank(BankType.ING_V1);
        b.setBookingDate(LocalDate.now());
        b.setPurpose("p");
        b.setCounterparty("c");
        b.setAmount(1L);
        b.setData(data);
        return b;
    }

    private TransactionLinkSuggestion newLink(BankTransaction b, Transaction t) {
        TransactionLinkSuggestion s = new TransactionLinkSuggestion();
        s.setBankTransaction(b);
        s.setTransaction(t);
        s.setProbability(1.0);
        return s;
    }
}
