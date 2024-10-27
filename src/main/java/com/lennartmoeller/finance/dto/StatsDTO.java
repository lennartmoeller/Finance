package com.lennartmoeller.finance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
@Setter
public class StatsDTO {

	private List<DailyStatsDTO> dailyStats;
	private CategoryStatsDTO incomeStats;
	private CategoryStatsDTO expenseStats;
	private @Nullable LocalDate startDate;
	private @Nullable LocalDate endDate;

	public static StatsDTO empty() {
		StatsDTO statsDTO = new StatsDTO();
		statsDTO.setDailyStats(List.of());
		statsDTO.setIncomeStats(CategoryStatsDTO.empty(null));
		statsDTO.setExpenseStats(CategoryStatsDTO.empty(null));
		return statsDTO;
	}

	@JsonProperty
	public RowStatsDTO getTotalStats() {
		Map<YearMonth, CellStatsDTO> totalStats = Stream.concat(
				this.getIncomeStats().getCategoryStats().stream(),
				this.getExpenseStats().getCategoryStats().stream()
			)
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

		return new RowStatsDTO(totalStats);
	}

}
