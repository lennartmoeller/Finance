package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.YearMonth;

@NoArgsConstructor
@Getter
@Setter
public class MonthlyStatsDTO {
	YearMonth month;
	Long surplus;
	Double smoothedSurplus;
	Double target;
	Double deviation;

	public MonthlyStatsDTO(YearMonth month) {
		this.month = month;
	}

	public MonthlyStatsDTO add(MonthlyStatsDTO other) {
		this.surplus += other.surplus;
		this.smoothedSurplus += other.smoothedSurplus;
		this.target += other.target;
		this.deviation += other.deviation;
		return this;
	}

}
