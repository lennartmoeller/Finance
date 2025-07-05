package com.lennartmoeller.finance.dto;

import javax.annotation.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountDTO {
    @Nullable
    private Long id;

    @Nullable
    private String label;

    @Nullable
    private String iban;

    @Nullable
    private Long startBalance;

    @Nullable
    private Boolean active;

    @Nullable
    private Boolean deposits;
}
