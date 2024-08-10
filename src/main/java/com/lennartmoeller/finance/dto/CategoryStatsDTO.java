package com.lennartmoeller.finance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
@Setter
public class CategoryStatsDTO {
	private final List<CategoryStatsNodeDTO> categoryStats;

	public static CategoryStatsDTO empty() {
		return new CategoryStatsDTO(List.of());
	}

	@JsonProperty
	public RowStatsDTO getTotalStats() {
		if (this.getCategoryStats().isEmpty()) {
			return RowStatsDTO.empty();
		}
		Map<YearMonth, CellStatsDTO> totalMonthly = categoryStats.stream()
			.map(CategoryStatsNodeDTO::getStats)
			.map(RowStatsDTO::getMonthly)
			.reduce(new HashMap<>(), (a, b) -> {
				b.forEach((k, v) -> a.merge(k, v, CellStatsDTO::add));
				return a;
			});
		return new RowStatsDTO(totalMonthly);
	}

}
