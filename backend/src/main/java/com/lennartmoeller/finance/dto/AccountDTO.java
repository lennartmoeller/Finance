package com.lennartmoeller.finance.dto;

import javax.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@Setter
public class AccountDTO {
    private @Nullable Long id;
    private String label;
    private @Nullable String iban;
    private Long startBalance;
    private Boolean active;
    private Boolean deposits;
}
