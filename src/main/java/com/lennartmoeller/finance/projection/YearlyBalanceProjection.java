package com.lennartmoeller.finance.projection;

import java.time.Year;

public interface YearlyBalanceProjection {

	int getYear();

	Long getBalance();

}
