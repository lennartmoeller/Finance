package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.YearMonth;

@Getter
@RequiredArgsConstructor
@Setter
public class MonthlyInflationCompensationStatsDTO {

	private YearMonth yearMonth;
	private double inflation;
	private double savings;
	private double balance;

}
