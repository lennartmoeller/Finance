package com.lennartmoeller.finance.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@Setter
public class CamtV8TransactionDTO extends BankTransactionDTO {
    private LocalDate valueDate;
    private String bookingText;
    private String creditorId;
    private String mandateReference;
    private String customerReference;
    private String collectorReference;
    private String directDebitOriginalAmount;
    private String refundFee;
    private String bic;
    private String currency;
    private String info;
}
