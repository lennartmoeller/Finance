package com.lennartmoeller.finance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class RowStatsDTO {
	private List<MonthlyCellStatsDTO> monthly;

	public static RowStatsDTO empty() {
		return new RowStatsDTO(List.of());
	}

	@JsonProperty
	public CellStatsDTO getMean() {
		if (this.getMonthly().isEmpty()) {
			return CellStatsDTO.empty();
		}
		CellStatsDTO mean = new CellStatsDTO();
		mean.setSurplus(StatsMetricDTO.mean(this.getMonthly().stream().map(monthlyCellStatsDTO -> monthlyCellStatsDTO.getStats().getSurplus()).toList()));
		mean.setTarget(0.0); // TODO
		return mean;
	}

}
