package com.lennartmoeller.finance.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@Setter
public class IngV1TransactionDTO extends BankTransactionDTO {
    private LocalDate valueDate;
    private String bookingText;
    private Long balance;
    private String balanceCurrency;
    private String amountCurrency;
}
