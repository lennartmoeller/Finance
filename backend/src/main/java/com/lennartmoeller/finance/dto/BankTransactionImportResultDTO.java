package com.lennartmoeller.finance.dto;

import java.util.List;
import lombok.Data;

@Data
public class BankTransactionImportResultDTO {
    private final List<BankTransactionDTO> saved;
    private final List<BankTransactionDTO> unsaved;
}
