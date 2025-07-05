package com.lennartmoeller.finance.dto;

import javax.annotation.Nonnull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountBalanceDTO {
    @Nonnull
    private Long accountId;

    @Nonnull
    private Long balance;
}
