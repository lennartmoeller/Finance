package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class StatsDTO {
	private List<DailyStatsDTO> dailyStats;
	private List<CategoryStatsNodeDTO> categoryStats;
	private RowStatsDTO incomeStats;
	private RowStatsDTO expenseStats;
	private RowStatsDTO surplusStats;
	private LocalDate startDate;
	private LocalDate endDate;

	public static StatsDTO empty() {
		StatsDTO statsDTO = new StatsDTO();
		statsDTO.setDailyStats(List.of());
		statsDTO.setCategoryStats(List.of());
		statsDTO.setIncomeStats(RowStatsDTO.empty());
		statsDTO.setExpenseStats(RowStatsDTO.empty());
		statsDTO.setSurplusStats(RowStatsDTO.empty());
		return statsDTO;
	}

}
