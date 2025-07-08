package com.lennartmoeller.finance.dto;

import java.time.LocalDate;
import javax.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@RequiredArgsConstructor
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class IngV1TransactionDTO extends BankTransactionDTO {
    private LocalDate valueDate;
    private String bookingText;
    private @Nullable Long balance;
    private String balanceCurrency;
    private String amountCurrency;
}
