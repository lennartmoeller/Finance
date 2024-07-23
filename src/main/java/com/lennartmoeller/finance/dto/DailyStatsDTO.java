package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class DailyStatsDTO {
	LocalDate date;
	Long balance;
	Double smoothedBalance;
}
