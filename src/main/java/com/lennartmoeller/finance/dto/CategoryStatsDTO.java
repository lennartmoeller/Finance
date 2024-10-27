package com.lennartmoeller.finance.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lennartmoeller.finance.util.DateRange;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.annotation.Nullable;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@Setter
public class CategoryStatsDTO {

	private final List<CategoryStatsNodeDTO> categoryStats;

	@JsonIgnore
	private final @Nullable DateRange dateRange;

	public static CategoryStatsDTO empty(@Nullable DateRange dateRange) {
		return new CategoryStatsDTO(List.of(), dateRange);
	}

	@JsonProperty
	public RowStatsDTO getTotalStats() {
		if (this.dateRange == null) {
			return RowStatsDTO.empty(null);
		}

		if (this.getCategoryStats().isEmpty()) {
			return RowStatsDTO.empty(dateRange);
		}

		Map<YearMonth, CellStatsDTO> totalMonthly = this.getCategoryStats().stream()
			.flatMap(categoryStatsNodeDTO -> categoryStatsNodeDTO.getStats().getMonthly().entrySet().stream())
			.collect(Collectors.groupingBy(
				Map.Entry::getKey,
				Collectors.mapping(Map.Entry::getValue, Collectors.toList())
			))
			.entrySet().stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				entry -> CellStatsDTO.add(entry.getValue())
			));

		return new RowStatsDTO(totalMonthly);
	}

}
