package com.lennartmoeller.finance.repository;

import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.projection.DailyBalanceProjection;
import com.lennartmoeller.finance.projection.MonthlyDepositsProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query(
            """
        SELECT t.date AS date,
               t.category AS category,
               SUM(t.amount) AS balance
        FROM Transaction t
        GROUP BY t.date, t.category
        ORDER BY t.date, t.category.label
        """)
    List<DailyBalanceProjection> getDailyBalances();

    @Query(
            """
        SELECT CONCAT(FUNCTION('YEAR', t.date), '-', LPAD(CAST(FUNCTION('MONTH', t.date) AS string), 2, '0')) AS yearMonth,
               SUM(t.amount) AS deposits
        FROM Transaction t
        WHERE t.account.deposits = true
        GROUP BY yearMonth
        """)
    List<MonthlyDepositsProjection> getMonthlyDeposits();
}
