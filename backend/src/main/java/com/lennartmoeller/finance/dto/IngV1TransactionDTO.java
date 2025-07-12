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
public class IngV1TransactionDTO extends BankTransactionDTO {
    private LocalDate valueDate;
    private String bookingText;
    private Long balance;
    private String balanceCurrency;
    private String amountCurrency;

    @Override
    public java.util.Map<String, String> buildDataMap() {
        java.util.Map<String, String> map = new java.util.LinkedHashMap<>(super.buildDataMap());
        map.put("valueDate", valueDate == null ? null : valueDate.toString());
        map.put("bookingText", bookingText);
        map.put("balance", balance == null ? null : balance.toString());
        map.put("balanceCurrency", balanceCurrency);
        map.put("amountCurrency", amountCurrency);
        return map;
    }
}
