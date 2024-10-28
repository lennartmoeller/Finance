package com.lennartmoeller.finance.repository;

import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.projection.DailyBalanceProjection;
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
		    AND (:yearMonths IS NULL OR CONCAT(FUNCTION('YEAR', t.date), '-', LPAD(CAST(FUNCTION('MONTH', t.date) AS string), 2, '0')) IN :yearMonths)
		""")
	List<Transaction> findFiltered(
		@Nullable List<Long> accountIds,
		@Nullable List<Long> categoryIds,
		@Nullable List<String> yearMonths);

	@Query("""
		    SELECT t.date AS date,
		           t.category AS category,
		           SUM(t.amount) AS balance
		    FROM Transaction t
		    GROUP BY t.date, t.category
		    ORDER BY t.date, t.category.label
		""")
	List<DailyBalanceProjection> getDailyBalances();

}
