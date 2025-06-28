package com.lennartmoeller.finance.dto;

import com.lennartmoeller.finance.model.BankType;
import java.time.LocalDate;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class BankTransactionDTO {
    private Long id;
    private BankType bank;
    private String iban;
    private LocalDate bookingDate;
    private String purpose;
    private String counterparty;
    private Long amount;
    private Map<String, String> data;
}
