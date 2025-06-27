package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@Setter
public class AccountBalanceDTO {

    private Long accountId;
    private Long balance;
}
