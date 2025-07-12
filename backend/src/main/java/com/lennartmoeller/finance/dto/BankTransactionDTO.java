package com.lennartmoeller.finance.dto;

import com.lennartmoeller.finance.model.BankType;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public abstract class BankTransactionDTO {
    private @Nullable Long id;
    private BankType bank;
    private String iban;
    private LocalDate bookingDate;
    private String purpose;
    private String counterparty;
    private Long amount;

    protected abstract BankType defaultBankType();

    public Map<String, String> toDataMap() {
        Map<String, String> map = new LinkedHashMap<>();
        BankType type = bank != null ? bank : defaultBankType();
        map.put("bank", type == null ? null : type.name());
        map.put("iban", iban);
        map.put("bookingDate", bookingDate == null ? null : bookingDate.toString());
        map.put("purpose", purpose);
        map.put("counterparty", counterparty);
        map.put("amount", amount == null ? null : amount.toString());
        return map;
    }
}
