package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
@Setter
public class DailyBalanceStatsDTO {

	private LocalDate date;
	private StatsMetricDTO balance;
	private StatsMetricDTO target;

}
