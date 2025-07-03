package com.lennartmoeller.finance.repository;

import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.projection.AccountBalanceProjection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(
            """
		    SELECT a.id as accountId, COALESCE(SUM(t.amount), 0) + a.startBalance as balance, COUNT(t.id) as transactionCount
		    FROM Account a
		    LEFT JOIN Transaction t ON t.account.id = a.id
		    GROUP BY a.id
		    ORDER BY a.id
		""")
    List<AccountBalanceProjection> getAccountBalances();

    @Query("""
		SELECT SUM(a.startBalance)
		FROM Account a
		""")
    Long getSummedStartBalance();

    Optional<Account> findByIban(String iban);
}
