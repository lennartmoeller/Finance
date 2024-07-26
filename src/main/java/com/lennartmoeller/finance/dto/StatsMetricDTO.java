package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class StatsMetricDTO {
	private Long raw;
	private Double smoothed;

	public static StatsMetricDTO empty() {
		StatsMetricDTO dto = new StatsMetricDTO();
		dto.raw = 0L;
		dto.smoothed = 0.0;
		return dto;
	}

	public static StatsMetricDTO add(StatsMetricDTO a, StatsMetricDTO b) {
		StatsMetricDTO result = new StatsMetricDTO();
		result.raw = a.getRaw() + b.getRaw();
		result.smoothed = a.getSmoothed() + b.getSmoothed();
		return result;
	}

	public StatsMetricDTO add(StatsMetricDTO other) {
		this.raw += other.getRaw();
		this.smoothed += other.getSmoothed();
		return this;
	}

}
