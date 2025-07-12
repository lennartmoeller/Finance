package com.lennartmoeller.finance.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
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

    @Override
    public java.util.Map<String, String> buildDataMap() {
        java.util.Map<String, String> map = new java.util.LinkedHashMap<>(super.buildDataMap());
        map.put("valueDate", valueDate == null ? null : valueDate.toString());
        map.put("bookingText", bookingText);
        map.put("creditorId", creditorId);
        map.put("mandateReference", mandateReference);
        map.put("customerReference", customerReference);
        map.put("collectorReference", collectorReference);
        map.put("directDebitOriginalAmount", directDebitOriginalAmount);
        map.put("refundFee", refundFee);
        map.put("bic", bic);
        map.put("currency", currency);
        map.put("info", info);
        return map;
    }
}
