package com.lennartmoeller.finance.dto;

import java.time.LocalDate;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class IngV1TransactionDTO extends BankTransactionDTO {
    @Nullable
    private LocalDate valueDate;

    @Nullable
    private String bookingText;

    @Nullable
    private Long balance;

    @Nullable
    private String balanceCurrency;

    @Nullable
    private String amountCurrency;
}
