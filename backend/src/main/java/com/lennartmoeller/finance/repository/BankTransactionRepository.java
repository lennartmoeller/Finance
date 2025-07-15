package com.lennartmoeller.finance.repository;

import com.lennartmoeller.finance.model.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {}
