package com.lennartmoeller.finance.dto;

import com.lennartmoeller.finance.model.BankType;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
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
    protected BankType defaultBankType() {
        return BankType.CAMT_V8;
    }

    @Override
    public Map<String, String> toDataMap() {
        Map<String, String> map = new LinkedHashMap<>(super.toDataMap());
        map.put("valueDate", getValueDate() == null ? null : getValueDate().toString());
        map.put("bookingText", getBookingText());
        map.put("creditorId", getCreditorId());
        map.put("mandateReference", getMandateReference());
        map.put("customerReference", getCustomerReference());
        map.put("collectorReference", getCollectorReference());
        map.put("directDebitOriginalAmount", getDirectDebitOriginalAmount());
        map.put("refundFee", getRefundFee());
        map.put("bic", getBic());
        map.put("currency", getCurrency());
        map.put("info", getInfo());
        return map;
    }
}
