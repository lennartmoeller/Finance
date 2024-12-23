package com.lennartmoeller.finance.projection;

public interface MonthlyDepositsProjection {

	String getYearMonth();

	Long getDeposits();

}
