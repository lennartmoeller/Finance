package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.List;

@Getter
@RequiredArgsConstructor
@Setter
public class PerformanceDTO {
	private double raw;
	private double smoothed;

	public static PerformanceDTO mean(List<PerformanceDTO> performanceDTOs) {
		PerformanceDTO sum = new PerformanceDTO();
		sum.setRaw(performanceDTOs.stream().mapToDouble(PerformanceDTO::getRaw).average().orElse(0));
		sum.setSmoothed(performanceDTOs.stream().mapToDouble(PerformanceDTO::getSmoothed).average().orElse(0));
		return sum;
	}

	public static ImmutableTriple<Double, Double, Double> calculateBounds(DescriptiveStatistics surplusDS) {
		double median = surplusDS.getPercentile(50);
		DescriptiveStatistics lowerSurpluses = new DescriptiveStatistics();
		DescriptiveStatistics upperSurpluses = new DescriptiveStatistics();
		for (double surplus : surplusDS.getValues()) {
			if (surplus < median) lowerSurpluses.addValue(surplus);
			else if (surplus > median) upperSurpluses.addValue(surplus);
		}
		double madLower = lowerSurpluses.getPercentile(25);
		double madUpper = upperSurpluses.getPercentile(75);
		if (Double.isNaN(madLower)) {
			madLower = median;
		}
		if (Double.isNaN(madUpper)) {
			madUpper = median;
		}
		return new ImmutableTriple<>(madLower, median, madUpper);
	}

	public static PerformanceDTO calculate(double rawValue, ImmutableTriple<Double, Double, Double> rawBounds, double smoothedValue, ImmutableTriple<Double, Double, Double> smoothedBounds) {
		PerformanceDTO performance = new PerformanceDTO();
		performance.setRaw(calculateForType(rawValue, rawBounds));
		performance.setSmoothed(calculateForType(smoothedValue, smoothedBounds));
		return performance;
	}

	private static double calculateForType(double value, ImmutableTriple<Double, Double, Double> bounds) {
		double performance;
		if (value >= bounds.right) {
			performance = 1.0;
		} else if (value <= bounds.left) {
			performance = 0.0;
		} else if (value == bounds.middle) {
			performance = 0.5;
		} else if (value < bounds.middle) {
			performance = 0.5 * (value - bounds.left) / (bounds.middle - bounds.left);
		} else {
			performance = 0.5 + 0.5 * (value - bounds.middle) / (bounds.right - bounds.middle);
		}
		return Math.clamp(performance, 0.0, 1.0);
	}

}
