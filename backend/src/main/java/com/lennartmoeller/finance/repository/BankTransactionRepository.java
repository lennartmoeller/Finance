package com.lennartmoeller.finance.repository;

import com.lennartmoeller.finance.model.BankTransaction;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {

    boolean existsByIbanAndBookingDateAndPurposeAndCounterpartyAndAmount(
            String iban, LocalDate bookingDate, String purpose, String counterparty, Long amount);
}
