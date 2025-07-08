package com.lennartmoeller.finance.dto;

import lombok.Data;

@Data
public class AccountBalanceDTO {
    private Long accountId;
    private Long balance;
}
