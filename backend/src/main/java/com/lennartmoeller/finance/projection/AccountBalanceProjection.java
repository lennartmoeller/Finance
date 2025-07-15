package com.lennartmoeller.finance.projection;

public interface AccountBalanceProjection {
    Long getAccountId();

    Long getBalance();

    Long getTransactionCount();

    Boolean getActive();
}
