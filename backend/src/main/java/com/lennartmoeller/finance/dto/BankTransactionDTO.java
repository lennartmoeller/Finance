package com.lennartmoeller.finance.dto;

import com.lennartmoeller.finance.model.BankType;
import java.time.LocalDate;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@Setter
public class BankTransactionDTO {
    private Long id;
    private BankType bank;
    private String iban;
    private LocalDate bookingDate;
    private String purpose;
    private String counterparty;
    private Long amount;
    private Map<String, String> data;
}
