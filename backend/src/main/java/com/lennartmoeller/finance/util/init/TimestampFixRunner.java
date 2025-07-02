package com.lennartmoeller.finance.util.init;

import com.lennartmoeller.finance.model.BaseModel;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import com.lennartmoeller.finance.repository.CategoryRepository;
import com.lennartmoeller.finance.repository.InflationRateRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TimestampFixRunner implements ApplicationRunner {
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final InflationRateRepository inflationRateRepository;

    @Override
    public void run(ApplicationArguments args) {
        Timestamp now = Timestamp.from(Instant.now());
        update(accountRepository, now);
        update(categoryRepository, now);
        update(transactionRepository, now);
        update(bankTransactionRepository, now);
        update(inflationRateRepository, now);
    }

    private <T extends BaseModel> void update(JpaRepository<T, Long> repository, Timestamp now) {
        List<T> entities = repository.findAll();
        boolean changed = false;
        for (T entity : entities) {
            boolean entityChanged = false;
            if (entity.getCreatedAt() == null
                    || entity.getCreatedAt().toInstant().equals(Instant.EPOCH)) {
                entity.setCreatedAt(now);
                entityChanged = true;
            }
            if (entity.getModifiedAt() == null
                    || entity.getModifiedAt().toInstant().equals(Instant.EPOCH)) {
                entity.setModifiedAt(now);
                entityChanged = true;
            }
            if (entityChanged) {
                changed = true;
            }
        }
        if (changed) {
            repository.saveAll(entities);
        }
    }
}
