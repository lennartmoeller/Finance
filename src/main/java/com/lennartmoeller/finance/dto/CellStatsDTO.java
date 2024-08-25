package com.lennartmoeller.finance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import javax.annotation.Nullable;
import java.util.List;

@Getter
@RequiredArgsConstructor
@Setter
public class CellStatsDTO {
	private StatsMetricDTO surplus;
	private Double target;
	private @Nullable PerformanceDTO performance;

	public static CellStatsDTO empty() {
		CellStatsDTO dto = new CellStatsDTO();
		dto.setSurplus(StatsMetricDTO.empty());
		dto.setTarget(0.0);
		dto.setPerformance(null);
		return dto;
	}

	public static CellStatsDTO add(List<CellStatsDTO> cellStatsDTOs) {
		CellStatsDTO result = CellStatsDTO.empty();
		result.setSurplus(StatsMetricDTO.add(cellStatsDTOs.stream().map(CellStatsDTO::getSurplus).toList()));
		result.setTarget(cellStatsDTOs.stream().mapToDouble(CellStatsDTO::getTarget).sum());
		result.setPerformance(PerformanceDTO.mean(cellStatsDTOs.stream().map(CellStatsDTO::getPerformance).toList()));
		return result;
	}

	@JsonProperty
	public StatsMetricDTO getDeviation() {
		StatsMetricDTO output = new StatsMetricDTO();
		output.setRaw(this.getSurplus().getRaw() + this.getTarget());
		output.setSmoothed(this.getSurplus().getSmoothed() + this.getTarget());
		return output;
	}

	public void calculatePerformance(ImmutableTriple<Double, Double, Double> rawBounds, ImmutableTriple<Double, Double, Double> smoothedBounds) {
		this.setPerformance(PerformanceDTO.calculate(
			this.getSurplus().getRaw(),
			rawBounds,
			this.getSurplus().getSmoothed(),
			smoothedBounds
		));
	}

}
