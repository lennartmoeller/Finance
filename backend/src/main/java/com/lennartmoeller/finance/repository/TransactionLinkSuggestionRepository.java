package com.lennartmoeller.finance.repository;

import com.lennartmoeller.finance.model.TransactionLinkSuggestion;
import java.util.List;
import javax.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionLinkSuggestionRepository extends JpaRepository<TransactionLinkSuggestion, Long> {
    @Query(
            """
                SELECT s
                FROM TransactionLinkSuggestion s
                WHERE (:bankTransactionIds IS NULL OR s.bankTransaction.id IN :bankTransactionIds)
                  AND (:transactionIds IS NULL OR s.transaction.id IN :transactionIds)
            """)
    List<TransactionLinkSuggestion> findAllByBankTransactionIdsAndTransactionIds(
            @Nullable List<Long> bankTransactionIds, @Nullable List<Long> transactionIds);

    List<TransactionLinkSuggestion> findAllByTransaction_Id(Long transactionId);

    List<TransactionLinkSuggestion> findAllByBankTransaction_Id(Long bankTransactionId);

    void deleteAllByTransaction_Id(Long transactionId);

    void deleteAllByBankTransaction_Id(Long bankTransactionId);
}
