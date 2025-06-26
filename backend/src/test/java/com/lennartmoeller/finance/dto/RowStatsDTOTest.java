package com.lennartmoeller.finance.dto;

import com.lennartmoeller.finance.util.DateRange;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RowStatsDTOTest {

	private static CellStatsDTO cell(double raw) {
		StatsMetricDTO m = new StatsMetricDTO();
		m.setRaw(raw);
		m.setSmoothed(raw);
		CellStatsDTO c = new CellStatsDTO();
		c.setSurplus(m);
		c.setTarget(0.0);
		PerformanceDTO p = new PerformanceDTO();
		c.setPerformance(p);
		return c;
	}

	@Test
	void testEmptyNull() {
		RowStatsDTO dto = RowStatsDTO.empty(null);
		assertTrue(dto.getMonthly().isEmpty());
	}

	@Test
	void testEmptyWithRange() {
		DateRange range = new DateRange(YearMonth.of(2023, 1), YearMonth.of(2023, 3));
		RowStatsDTO dto = RowStatsDTO.empty(range);
		assertEquals(3, dto.getMonthly().size());
		assertTrue(dto.getMonthly().values().stream().allMatch(c -> c.getSurplus().getRaw() == 0.0));
	}

	@Test
	void testGetMean() {
		YearMonth m1 = YearMonth.of(2023, 1);
		YearMonth m2 = YearMonth.of(2023, 2);
		RowStatsDTO dto = new RowStatsDTO(Map.of(m1, cell(2), m2, cell(6)));
		CellStatsDTO mean = dto.getMean();
		assertEquals(4.0, mean.getSurplus().getRaw());
		assertEquals(0.0, mean.getTarget());
	}

	@Test
	void testGetMeanEmpty() {
		RowStatsDTO dto = new RowStatsDTO(Map.of());
		CellStatsDTO mean = dto.getMean();
		assertEquals(0.0, mean.getSurplus().getRaw());
		assertEquals(0.0, mean.getTarget());
	}
}
