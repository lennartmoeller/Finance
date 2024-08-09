package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class StatsMetricDTO {
	private double raw;
	private double smoothed;

	public static StatsMetricDTO empty() {
		StatsMetricDTO dto = new StatsMetricDTO();
		dto.setRaw(0.0);
		dto.setSmoothed(0.0);
		return dto;
	}

	public static StatsMetricDTO add(StatsMetricDTO a, StatsMetricDTO b) {
		StatsMetricDTO result = new StatsMetricDTO();
		result.setRaw(a.getRaw() + b.getRaw());
		result.setSmoothed(a.getSmoothed() + b.getSmoothed());
		return result;
	}

	public static StatsMetricDTO mean(List<StatsMetricDTO> statsMetrics) {
		StatsMetricDTO output = statsMetrics.stream().reduce(StatsMetricDTO.empty(), StatsMetricDTO::add);
		output.setRaw(output.getRaw() / statsMetrics.size());
		output.setSmoothed(output.getSmoothed() / statsMetrics.size());
		return output;
	}

}
