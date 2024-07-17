package com.lennartmoeller.finance.projection;

import java.time.LocalDate;

public interface DailyBalanceProjection {

	LocalDate getDate();

	Long getBalance();

}
