package com.lennartmoeller.finance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import javax.annotation.Nullable;

@Getter
@Setter
@RequiredArgsConstructor
public class CellStatsDTO {
	private StatsMetricDTO surplus;
	private Double target;
	private @Nullable PerformanceDTO performance;

	public static CellStatsDTO empty() {
		CellStatsDTO dto = new CellStatsDTO();
		dto.setSurplus(StatsMetricDTO.empty());
		dto.setTarget(0.0);
		return dto;
	}

	public static CellStatsDTO add(CellStatsDTO a, CellStatsDTO b) {
		CellStatsDTO result = new CellStatsDTO();
		result.setSurplus(StatsMetricDTO.add(a.getSurplus(), b.getSurplus()));
		result.setTarget(a.getTarget() + b.getTarget());
		return result;
	}

	@JsonProperty
	public StatsMetricDTO getDeviation() {
		StatsMetricDTO output = new StatsMetricDTO();
		output.setRaw(this.getSurplus().getRaw() + this.getTarget());
		output.setSmoothed(this.getSurplus().getSmoothed() + this.getTarget());
		return output;
	}

	public void calculatePerformance(DescriptiveStatistics rawSurpluses, DescriptiveStatistics smoothedSurpluses) {
		this.setPerformance(PerformanceDTO.generate(rawSurpluses, smoothedSurpluses, this.getSurplus().getRaw(), this.getSurplus().getSmoothed()));
	}

}
