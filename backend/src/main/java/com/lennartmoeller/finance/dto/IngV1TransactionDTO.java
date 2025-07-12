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
public class IngV1TransactionDTO extends BankTransactionDTO {
    private LocalDate valueDate;
    private String bookingText;
    private Long balance;
    private String balanceCurrency;
    private String amountCurrency;

    @Override
    protected BankType defaultBankType() {
        return BankType.ING_V1;
    }

    @Override
    public Map<String, String> toDataMap() {
        Map<String, String> map = new LinkedHashMap<>(super.toDataMap());
        map.put("valueDate", getValueDate() == null ? null : getValueDate().toString());
        map.put("bookingText", getBookingText());
        map.put("balance", getBalance() == null ? null : getBalance().toString());
        map.put("balanceCurrency", getBalanceCurrency());
        map.put("amountCurrency", getAmountCurrency());
        return map;
    }
}
