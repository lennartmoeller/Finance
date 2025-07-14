package com.lennartmoeller.finance.dto;

import com.lennartmoeller.finance.model.BankType;
import java.time.LocalDate;
import javax.annotation.Nullable;
import lombok.Data;

@Data
public abstract class BankTransactionDTO {
    private @Nullable Long id;
    private BankType bank;
    private String iban;
    private LocalDate bookingDate;
    private String purpose;
    private String counterparty;
    private Long amount;
}
