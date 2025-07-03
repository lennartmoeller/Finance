package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@Setter
public class AccountDTO {
    private Long id;
    private String label;
    private String iban;
    private Long startBalance;
    private Boolean active;
    private Boolean deposits;
}
