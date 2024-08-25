package com.lennartmoeller.finance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.YearMonth;
import java.util.Map;

@Getter
@RequiredArgsConstructor
@Setter
public class RowStatsDTO {
	private final Map<YearMonth, CellStatsDTO> monthly;

	public static RowStatsDTO empty() {
		return new RowStatsDTO(Map.of());
	}

	@JsonProperty
	public CellStatsDTO getMean() {
		if (this.getMonthly().isEmpty()) {
			return CellStatsDTO.empty();
		}

		CellStatsDTO mean = new CellStatsDTO();
		mean.setSurplus(StatsMetricDTO.mean(this.getMonthly().values().stream().map(CellStatsDTO::getSurplus).toList()));
		mean.setTarget(0.0); // TODO
		return mean;
	}

}
