package com.lennartmoeller.finance.repository;

import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.projection.DailyBalanceProjection;
import com.lennartmoeller.finance.projection.MonthlyBalanceProjection;
import com.lennartmoeller.finance.projection.YearlyBalanceProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	@Query("SELECT t FROM Transaction t WHERE EXTRACT(YEAR FROM t.date) = :year AND EXTRACT(MONTH FROM t.date) = :month")
	List<Transaction> findAllByYearMonth(int year, int month);

	@Query("""
		    SELECT t.date AS date, SUM(t.amount) AS balance
		    FROM Transaction t
		    GROUP BY t.date
		    ORDER BY t.date
		""")
	List<DailyBalanceProjection> getDailyBalances();

	@Query("""
			SELECT t.date AS date,
			       SUM(t.amount) AS balance
			FROM Transaction t
			WHERE t.category.smoothType = com.lennartmoeller.finance.model.CategorySmoothType.DAILY
			GROUP BY t.date
			ORDER BY t.date
		""")
	List<DailyBalanceProjection> getBalancesForDailySmoothedTransactions();

	@Query("""
			SELECT EXTRACT(YEAR FROM t.date) AS year,
			       EXTRACT(MONTH FROM t.date) AS month,
			       SUM(t.amount) AS balance
			FROM Transaction t
			WHERE t.category.smoothType = com.lennartmoeller.finance.model.CategorySmoothType.MONTHLY
			GROUP BY EXTRACT(YEAR FROM t.date), EXTRACT(MONTH FROM t.date)
			ORDER BY EXTRACT(YEAR FROM t.date), EXTRACT(MONTH FROM t.date)
		""")
	List<MonthlyBalanceProjection> getBalancesForMonthlySmoothedTransactions();

	@Query("""
			SELECT EXTRACT(YEAR FROM t.date) AS year,
			       SUM(t.amount) AS balance
			FROM Transaction t
			WHERE t.category.smoothType = com.lennartmoeller.finance.model.CategorySmoothType.YEARLY
			GROUP BY EXTRACT(YEAR FROM t.date)
			ORDER BY EXTRACT(YEAR FROM t.date)
		""")
	List<YearlyBalanceProjection> getBalancesForYearlySmoothedTransactions();

}
