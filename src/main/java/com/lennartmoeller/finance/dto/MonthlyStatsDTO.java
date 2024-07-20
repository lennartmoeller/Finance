package com.lennartmoeller.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.YearMonth;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MonthlyStatsDTO {
	YearMonth month;
	Long surplus;
	Long smoothedSurplus;
	Long target;
	Long deviation;
}
