package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import javax.annotation.Nullable;
import java.time.YearMonth;

@RequiredArgsConstructor
@Getter
@Setter
public class MonthlyStatsDTO {
	private final YearMonth month;
	private StatsMetricDTO surplus;
	private Double target;
	private StatsMetricDTO deviation;
	private @Nullable PerformanceDTO performance;

	public static MonthlyStatsDTO empty(YearMonth month) {
		MonthlyStatsDTO dto = new MonthlyStatsDTO(month);
		dto.surplus = StatsMetricDTO.empty();
		dto.target = 0.0;
		dto.deviation = StatsMetricDTO.empty();
		return dto;
	}

	public static MonthlyStatsDTO add(MonthlyStatsDTO a, MonthlyStatsDTO b) {
		if (!a.getMonth().equals(b.getMonth())) {
			throw new IllegalArgumentException("Cannot add MonthlyStatsDTO with different months");
		}
		MonthlyStatsDTO result = new MonthlyStatsDTO(a.getMonth());
		result.surplus = StatsMetricDTO.add(a.getSurplus(), b.getSurplus());
		result.target = a.getTarget() + b.getTarget();
		result.deviation = StatsMetricDTO.add(a.getDeviation(), b.getDeviation());
		return result;
	}

	public void calculatePerformance(DescriptiveStatistics rawSurpluses, DescriptiveStatistics smoothedSurpluses) {
		this.performance = PerformanceDTO.generate(rawSurpluses, smoothedSurpluses, this.surplus.getRaw(), this.surplus.getSmoothed());
	}

}
