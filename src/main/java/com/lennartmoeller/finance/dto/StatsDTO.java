package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class StatsDTO {
	private List<DailyStatsDTO> dailyStats;
	private List<CategoryStatsNodeDTO> categoryStats;
	private List<MonthlyStatsDTO> incomeStats;
	private List<MonthlyStatsDTO> expenseStats;
	private List<MonthlyStatsDTO> surplusStats;
	private LocalDate startDate;
	private LocalDate endDate;

	public static StatsDTO empty() {
		StatsDTO statsDTO = new StatsDTO();
		statsDTO.setDailyStats(Collections.emptyList());
		statsDTO.setCategoryStats(Collections.emptyList());
		statsDTO.setIncomeStats(Collections.emptyList());
		statsDTO.setExpenseStats(Collections.emptyList());
		statsDTO.setSurplusStats(Collections.emptyList());
		return statsDTO;
	}
}
