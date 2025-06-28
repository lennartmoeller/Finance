package com.lennartmoeller.finance.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IngV1TransactionDTO extends BankTransactionDTO {
    private LocalDate valueDate;
    private String bookingText;
    private Long balance;
    private String balanceCurrency;
    private String amountCurrency;
}
