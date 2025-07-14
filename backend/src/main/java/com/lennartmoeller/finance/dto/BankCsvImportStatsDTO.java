package com.lennartmoeller.finance.dto;

import lombok.Data;

@Data
public class BankCsvImportStatsDTO {
    private int imports;
    private int duplicates;
    private int errors;
}
