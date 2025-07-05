package com.lennartmoeller.finance.dto;

import java.util.List;
import javax.annotation.Nonnull;
import lombok.Data;

@Data
public class BankTransactionImportResultDTO {
    @Nonnull
    private final List<BankTransactionDTO> saved;

    @Nonnull
    private final List<BankTransactionDTO> unsaved;
}
