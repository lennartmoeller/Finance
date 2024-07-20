package com.lennartmoeller.finance.repository;

import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.projection.DailyBalanceProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	@Query("SELECT t FROM Transaction t WHERE EXTRACT(YEAR FROM t.date) = :year AND EXTRACT(MONTH FROM t.date) = :month")
	List<Transaction> findAllByYearMonth(int year, int month);

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
