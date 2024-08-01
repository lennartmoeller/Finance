package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import javax.annotation.Nullable;
import java.time.YearMonth;
import java.util.function.ToDoubleFunction;

@RequiredArgsConstructor
@Getter
@Setter
@Log
public class MonthlyStatsDTO {
	private final YearMonth month;
	private StatsMetricDTO incomes;
	private StatsMetricDTO expenses;
	private StatsMetricDTO surplus;
	private Double target;
	private StatsMetricDTO deviation;
	private @Nullable StatsMetricDTO performance;

	public static MonthlyStatsDTO empty(YearMonth month) {
		MonthlyStatsDTO dto = new MonthlyStatsDTO(month);
		dto.incomes = StatsMetricDTO.empty();
		dto.expenses = StatsMetricDTO.empty();
		dto.surplus = StatsMetricDTO.empty();
		dto.target = 0.0;
		dto.deviation = StatsMetricDTO.empty();
		dto.performance = null;
		return dto;
	}

	public MonthlyStatsDTO add(MonthlyStatsDTO other) {
		this.incomes.add(other.getIncomes());
		this.expenses.add(other.getExpenses());
		this.surplus.add(other.getSurplus());
		this.target += other.getTarget();
		this.deviation.add(other.getDeviation());
		this.performance = null;
		return this;
	}

	public void calculatePerformances(DescriptiveStatistics rawSurpluses, DescriptiveStatistics smoothedSurpluses) {
		this.performance = new StatsMetricDTO();
		this.performance.setRaw(calculatePerformance(rawSurpluses, StatsMetricDTO::getRaw));
		this.performance.setSmoothed(calculatePerformance(smoothedSurpluses, StatsMetricDTO::getSmoothed));
	}

	private double calculatePerformance(DescriptiveStatistics surpluses, ToDoubleFunction<StatsMetricDTO> getter) {
		double surplusValue = getter.applyAsDouble(this.surplus);
		double mean = surpluses.getMean();
		double standardDeviation = surpluses.getStandardDeviation();
		if (standardDeviation == 0.0) {
			if (surplusValue == mean) {
				return 0.5;
			}
			return surplusValue > mean ? 1.0 : 0.0;
		}
		double normalizedSurplus = (1.0 + (surplusValue - mean) / standardDeviation) / 2.0;
		return Math.clamp(normalizedSurplus, 0.0, 1.0);
	}

}
