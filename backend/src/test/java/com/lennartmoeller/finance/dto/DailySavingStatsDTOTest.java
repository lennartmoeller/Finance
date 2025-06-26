package com.lennartmoeller.finance.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DailySavingStatsDTOTest {
	@Test
	void gettersAndSetters() {
		DailySavingStatsDTO dto = new DailySavingStatsDTO();
		LocalDate date = LocalDate.of(2024, 1, 1);
		StatsMetricDTO bal = new StatsMetricDTO();
		StatsMetricDTO target = new StatsMetricDTO();
		dto.setDate(date);
		dto.setBalance(bal);
		dto.setTarget(target);

		assertEquals(date, dto.getDate());
		assertSame(bal, dto.getBalance());
		assertSame(target, dto.getTarget());
	}
}
