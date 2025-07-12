package com.lennartmoeller.finance.dto;

import com.lennartmoeller.finance.model.BankType;
import java.time.LocalDate;
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

    public java.util.Map<String, String> buildDataMap() {
        java.util.Map<String, String> map = new java.util.LinkedHashMap<>();
        map.put("bank", bank == null ? null : bank.name());
        map.put("iban", iban);
        map.put("bookingDate", bookingDate == null ? null : bookingDate.toString());
        map.put("purpose", purpose);
        map.put("counterparty", counterparty);
        map.put("amount", amount == null ? null : amount.toString());
        return map;
    }

    public void fillFromMap(java.util.Map<String, String> map) {
        if (map == null) {
            return;
        }
        java.util.Optional.ofNullable(map.get("bank"))
                .ifPresent(v -> setBank(com.lennartmoeller.finance.model.BankType.valueOf(v)));
        if (map.containsKey("iban")) {
            setIban(map.get("iban"));
        }
        java.util.Optional.ofNullable(map.get("bookingDate"))
                .map(java.time.LocalDate::parse)
                .ifPresent(this::setBookingDate);
        if (map.containsKey("purpose")) {
            setPurpose(map.get("purpose"));
        }
        if (map.containsKey("counterparty")) {
            setCounterparty(map.get("counterparty"));
        }
        java.util.Optional.ofNullable(map.get("amount"))
                .map(java.lang.Long::valueOf)
                .ifPresent(this::setAmount);
    }
}
