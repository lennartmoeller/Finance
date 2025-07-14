package com.lennartmoeller.finance.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IngV1TransactionDTO extends BankTransactionDTO {
    private LocalDate valueDate;
    private String bookingText;
    private Long balance;
    private String balanceCurrency;
    private String amountCurrency;
}
