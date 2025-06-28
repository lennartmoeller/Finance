package com.lennartmoeller.finance.repository;

import com.lennartmoeller.finance.model.BankTransaction;
import java.time.LocalDate;
import javax.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {

    @Query("select count(bt) > 0 from BankTransaction bt" + " where bt.iban = :iban"
            + " and bt.bookingDate = :bookingDate"
            + " and bt.purpose = :purpose"
            + " and bt.counterparty = :counterparty"
            + " and bt.amount = :amount")
    boolean existsDuplicate(
            @Nonnull String iban,
            @Nonnull LocalDate bookingDate,
            @Nonnull String purpose,
            @Nonnull String counterparty,
            @Nonnull Long amount);
}
