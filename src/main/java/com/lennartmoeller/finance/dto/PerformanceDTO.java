package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

@NoArgsConstructor
@Getter
@Setter
public class PerformanceDTO {
	private double raw;
	private double smoothed;

	public static PerformanceDTO generate(DescriptiveStatistics rawSurpluses, DescriptiveStatistics smoothedSurpluses, double rawSurplus, double smoothedSurplus) {
		PerformanceDTO performanceDTO = new PerformanceDTO();
		performanceDTO.setRaw(calculatePerformance(rawSurpluses, rawSurplus));
		performanceDTO.setSmoothed(calculatePerformance(smoothedSurpluses, smoothedSurplus));
		return performanceDTO;
	}

	private static double calculatePerformance(DescriptiveStatistics surpluses, double surplus) {
		double mean = surpluses.getMean();
		double standardDeviation = surpluses.getStandardDeviation();
		if (standardDeviation == 0.0) {
			if (surplus == mean) {
				return 0.5;
			}
			return surplus > mean ? 1.0 : 0.0;
		}
		double normalizedSurplus = (1.0 + (surplus - mean) / standardDeviation) / 2.0;
		return Math.clamp(normalizedSurplus, 0.0, 1.0);
	}

}
