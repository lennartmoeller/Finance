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
public class CamtV8TransactionDTO extends BankTransactionDTO {
    @Nullable
    private LocalDate valueDate;

    @Nullable
    private String bookingText;

    @Nullable
    private String creditorId;

    @Nullable
    private String mandateReference;

    @Nullable
    private String customerReference;

    @Nullable
    private String collectorReference;

    @Nullable
    private String directDebitOriginalAmount;

    @Nullable
    private String refundFee;

    @Nullable
    private String bic;

    @Nullable
    private String currency;

    @Nullable
    private String info;
}
