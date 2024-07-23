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
	List<DailyStatsDTO> dailyStats = Collections.emptyList();
	List<CategoryStatsNodeDTO> categoryStats = Collections.emptyList();
	List<MonthlyStatsDTO> monthlyStats = Collections.emptyList();
	LocalDate startDate;
	LocalDate endDate;
}
