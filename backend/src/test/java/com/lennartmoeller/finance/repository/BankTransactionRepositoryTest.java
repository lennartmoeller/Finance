package com.lennartmoeller.finance.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.BankType;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(
        properties = {
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.properties.hibernate.hbm2ddl.import_files="
        })
class BankTransactionRepositoryTest {
    @Autowired
    private BankTransactionRepository repository;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void findAllDatasReturnsSavedMaps() {
        Account account = new Account();
        account.setLabel("acc");
        account.setStartBalance(0L);
        account = accountRepository.save(account);

        BankTransaction bt1 = new BankTransaction();
        bt1.setBank(BankType.ING_V1);
        bt1.setAccount(account);
        bt1.setBookingDate(LocalDate.of(2024, 1, 1));
        bt1.setPurpose("p1");
        bt1.setCounterparty("c1");
        bt1.setAmount(1L);
        bt1.setData(Map.of("k1", "v1"));

        BankTransaction bt2 = new BankTransaction();
        bt2.setBank(BankType.ING_V1);
        bt2.setAccount(account);
        bt2.setBookingDate(LocalDate.of(2024, 1, 2));
        bt2.setPurpose("p2");
        bt2.setCounterparty("c2");
        bt2.setAmount(2L);
        bt2.setData(Map.of("k2", "v2"));

        repository.save(bt1);
        repository.save(bt2);

        List<Map<String, String>> result = repository.findAllDatas();

        assertThat(result).containsExactlyInAnyOrder(bt1.getData(), bt2.getData());
    }
}
