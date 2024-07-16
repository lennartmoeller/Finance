package com.lennartmoeller.finance.repository;

import com.lennartmoeller.finance.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
