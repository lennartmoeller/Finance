package com.lennartmoeller.finance.repository;

import com.lennartmoeller.finance.model.BankTransaction;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    @Query("SELECT bt.data FROM BankTransaction bt")
    List<Map<String, String>> findAllDatas();
}
