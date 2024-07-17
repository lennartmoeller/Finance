package com.lennartmoeller.finance.projection;

public interface MonthlyBalanceProjection {

	int getYear();

	int getMonth();

	Long getBalance();

}
