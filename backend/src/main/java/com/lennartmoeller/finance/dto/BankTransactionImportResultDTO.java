package com.lennartmoeller.finance.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@Setter
public class BankTransactionImportResultDTO {
    private final List<BankTransactionDTO> saved;
    private final List<BankTransactionDTO> unsaved;
}
