package com.lennartmoeller.finance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import javax.annotation.Nullable;

@Getter
@RequiredArgsConstructor
@Setter
public class CellStatsDTO {
	private StatsMetricDTO surplus;
	private Double target;
	private @Nullable StatsMetricDTO performance;

	public static CellStatsDTO empty() {
		CellStatsDTO dto = new CellStatsDTO();
		dto.setSurplus(StatsMetricDTO.empty());
		dto.setTarget(0.0);
		dto.setPerformance(null);
		return dto;
	}

	public static CellStatsDTO add(CellStatsDTO a, CellStatsDTO b) {
		CellStatsDTO result = new CellStatsDTO();
		result.setSurplus(StatsMetricDTO.add(a.getSurplus(), b.getSurplus()));
		result.setTarget(a.getTarget() + b.getTarget());
		result.setPerformance(null);
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
		StatsMetricDTO statsMetricDTO = new StatsMetricDTO();
		statsMetricDTO.setRaw(calculatePerformanceType(rawSurpluses, this.getSurplus().getRaw()));
		statsMetricDTO.setSmoothed(calculatePerformanceType(smoothedSurpluses, this.getSurplus().getSmoothed()));
		this.setPerformance(statsMetricDTO);
	}

	private double calculatePerformanceType(DescriptiveStatistics surpluses, double surplus) {
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
