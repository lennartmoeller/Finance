package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.YearMonth;

@RequiredArgsConstructor
@Getter
@Setter
public class MonthlyStatsDTO {
	private final YearMonth month;
	private StatsMetricDTO incomes;
	private StatsMetricDTO expenses;
	private StatsMetricDTO surplus;
	private Double target;
	private StatsMetricDTO deviation;

	public static MonthlyStatsDTO empty(YearMonth month) {
		MonthlyStatsDTO dto = new MonthlyStatsDTO(month);
		dto.incomes = StatsMetricDTO.empty();
		dto.expenses = StatsMetricDTO.empty();
		dto.surplus = StatsMetricDTO.empty();
		dto.target = 0.0;
		dto.deviation = StatsMetricDTO.empty();
		return dto;
	}

	public MonthlyStatsDTO add(MonthlyStatsDTO other) {
		this.incomes.add(other.getIncomes());
		this.expenses.add(other.getExpenses());
		this.surplus.add(other.getSurplus());
		this.target += other.getTarget();
		this.deviation.add(other.getDeviation());
		return this;
	}

}
