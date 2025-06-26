package com.lennartmoeller.finance.repository;

import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.projection.DailyBalanceProjection;
import com.lennartmoeller.finance.projection.MonthlyDepositsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.annotation.Nullable;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	@Query("""
		    SELECT t
		    FROM Transaction t
                    WHERE (:accountIds IS NULL OR t.account.id IN :accountIds)
                    AND (:categoryIds IS NULL OR t.category.id IN :categoryIds)
                    AND (:yearMonths IS NULL OR FUNCTION('TO_CHAR', t.date, 'YYYY-MM') IN :yearMonths)
                    AND (:pinned IS NULL OR t.pinned = :pinned)
                """)
        List<Transaction> findFiltered(
                @Nullable List<Long> accountIds,
                @Nullable List<Long> categoryIds,
                @Nullable List<String> yearMonths,
                @Nullable Boolean pinned);

	@Query("""
		    SELECT t.date AS date,
		           t.category AS category,
		           SUM(t.amount) AS balance
		    FROM Transaction t
		    GROUP BY t.date, t.category
		    ORDER BY t.date, t.category.label
		""")
	List<DailyBalanceProjection> getDailyBalances();

	@Query("""
		    SELECT CONCAT(FUNCTION('YEAR', t.date), '-', LPAD(CAST(FUNCTION('MONTH', t.date) AS string), 2, '0')) AS yearMonth,
		           SUM(t.amount) AS deposits
		    FROM Transaction t
		    WHERE t.account.deposits = true
		    GROUP BY yearMonth
		""")
	List<MonthlyDepositsProjection> getMonthlyDeposits();

}
