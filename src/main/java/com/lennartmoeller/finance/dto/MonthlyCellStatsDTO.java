package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.YearMonth;

@RequiredArgsConstructor
@Getter
public class MonthlyCellStatsDTO {
	private final YearMonth month;
	private final CellStatsDTO stats;

	public static MonthlyCellStatsDTO empty(YearMonth month) {
		return new MonthlyCellStatsDTO(month, CellStatsDTO.empty());
	}

	public static MonthlyCellStatsDTO add(MonthlyCellStatsDTO a, MonthlyCellStatsDTO b) {
		if (!a.getMonth().equals(b.getMonth())) {
			throw new IllegalArgumentException("Cannot add MonthlyStatsDTO with different months");
		}
		return new MonthlyCellStatsDTO(
			a.getMonth(),
			CellStatsDTO.add(a.getStats(), b.getStats())
		);
	}

}
