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
	private List<DailyStatsDTO> dailyStats = Collections.emptyList();
	private List<CategoryStatsNodeDTO> categoryStats = Collections.emptyList();
	private List<MonthlyStatsDTO> monthlyStats = Collections.emptyList();
	private LocalDate startDate;
	private LocalDate endDate;
}
