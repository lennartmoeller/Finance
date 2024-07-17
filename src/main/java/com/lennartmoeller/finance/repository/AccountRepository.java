package com.lennartmoeller.finance.repository;

import com.lennartmoeller.finance.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<Account, Long> {

	@Query("""
			SELECT SUM(a.startBalance)
			FROM Account a
		""")
	Long getSummedStartBalance();

}
