package com.lennartmoeller.finance.dto;

import com.lennartmoeller.finance.model.BankType;
import java.time.LocalDate;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@RequiredArgsConstructor
@Setter
@SuperBuilder
public class BankTransactionDTO {
    private @Nullable Long id;
    private BankType bank;
    private @Nullable String iban;
    private LocalDate bookingDate;
    private String purpose;
    private String counterparty;
    private @Nullable Long amount;
    private @Nullable Map<String, String> data;
}
