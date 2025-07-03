package com.lennartmoeller.finance.dto;

import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BankTransactionImportResultDTO {
    private final List<BankTransactionDTO> saved;
    private final List<BankTransactionDTO> unsaved;
}
