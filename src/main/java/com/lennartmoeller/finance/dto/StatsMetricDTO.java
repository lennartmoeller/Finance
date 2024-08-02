package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class StatsMetricDTO {
	private double raw;
	private double smoothed;

	public static StatsMetricDTO empty() {
		StatsMetricDTO dto = new StatsMetricDTO();
		dto.raw = 0.0;
		dto.smoothed = 0.0;
		return dto;
	}

	public static StatsMetricDTO add(StatsMetricDTO a, StatsMetricDTO b) {
		StatsMetricDTO result = new StatsMetricDTO();
		result.raw = a.getRaw() + b.getRaw();
		result.smoothed = a.getSmoothed() + b.getSmoothed();
		return result;
	}

}
