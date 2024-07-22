package com.lennartmoeller.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StatsDTO {
	List<DailyStatsDTO> dailyStats;
	List<CategoryStatsNodeDTO> categoryStats;
	List<MonthlyStatsDTO> monthlyStats;
	LocalDate startDate;
	LocalDate endDate;
}
