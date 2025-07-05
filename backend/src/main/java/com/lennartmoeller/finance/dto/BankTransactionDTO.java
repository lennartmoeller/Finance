package com.lennartmoeller.finance.dto;

import com.lennartmoeller.finance.model.BankType;
import java.time.LocalDate;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class BankTransactionDTO {
    @Nullable
    private Long id;

    @Nullable
    private BankType bank;

    @Nullable
    private String iban;

    @Nullable
    private LocalDate bookingDate;

    @Nullable
    private String purpose;

    @Nullable
    private String counterparty;

    @Nullable
    private Long amount;

    @Nullable
    private Map<String, String> data;
}
