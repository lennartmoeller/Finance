package com.lennartmoeller.finance.repository;

import com.lennartmoeller.finance.model.BankTransaction;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    boolean existsByData(Map<String, String> data);

    List<BankTransaction> findAllByDataIn(Collection<Map<String, String>> data);
}
