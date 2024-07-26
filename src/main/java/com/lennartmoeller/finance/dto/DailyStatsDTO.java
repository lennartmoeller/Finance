package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class DailyStatsDTO {
	private LocalDate date;
	private Long balance;
	private Double smoothedBalance;
}
