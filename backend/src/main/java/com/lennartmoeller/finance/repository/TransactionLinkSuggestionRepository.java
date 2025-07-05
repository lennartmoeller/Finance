package com.lennartmoeller.finance.repository;

import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.model.TransactionLinkSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionLinkSuggestionRepository extends JpaRepository<TransactionLinkSuggestion, Long> {
    boolean existsByBankTransactionAndTransaction(BankTransaction bankTransaction, Transaction transaction);
}
