package com.lennartmoeller.finance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@RequiredArgsConstructor
@Setter
public class StatsDTO {
	private List<DailyStatsDTO> dailyStats;
	private CategoryStatsDTO incomeStats;
	private CategoryStatsDTO expenseStats;
	private LocalDate startDate;
	private LocalDate endDate;

	public static StatsDTO empty() {
		StatsDTO statsDTO = new StatsDTO();
		statsDTO.setDailyStats(List.of());
		statsDTO.setIncomeStats(CategoryStatsDTO.empty());
		statsDTO.setExpenseStats(CategoryStatsDTO.empty());
		statsDTO.setStartDate(LocalDate.now());
		statsDTO.setEndDate(LocalDate.now());
		return statsDTO;
	}

	@JsonProperty
	public RowStatsDTO getTotalStats() {
		return RowStatsDTO.add(
			this.getIncomeStats().getTotalStats(),
			this.getExpenseStats().getTotalStats()
		);
	}

}
