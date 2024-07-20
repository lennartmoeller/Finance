package com.lennartmoeller.finance.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeUtilsTest {

	@Test
	void calculateOverlapDays() {
		assertEquals(0,
			TimeUtils.calculateOverlapDays(
				LocalDate.of(2024, 1, 1),
				LocalDate.of(2024, 1, 1),
				LocalDate.of(2024, 1, 2),
				LocalDate.of(2024, 1, 2)
			)
		);
		assertEquals(0,
			TimeUtils.calculateOverlapDays(
				LocalDate.of(2024, 1, 1),
				LocalDate.of(2024, 1, 2),
				LocalDate.of(2024, 1, 3),
				LocalDate.of(2024, 1, 4)
			)
		);
		assertEquals(1,
			TimeUtils.calculateOverlapDays(
				LocalDate.of(2024, 1, 1),
				LocalDate.of(2024, 1, 1),
				LocalDate.of(2024, 1, 1),
				LocalDate.of(2024, 1, 1)
			)
		);
		assertEquals(7,
			TimeUtils.calculateOverlapDays(
				LocalDate.of(2023, 12, 1),
				LocalDate.of(2024, 1, 31),
				LocalDate.of(2023, 12, 28),
				LocalDate.of(2024, 1, 3)
			)
		);
		assertEquals(16,
			TimeUtils.calculateOverlapDays(
				LocalDate.of(2023, 1, 1),
				LocalDate.of(2023, 12, 20),
				LocalDate.of(2023, 12, 5),
				LocalDate.of(2024, 1, 31)
			)
		);
	}

	@Test
	void calculateOverlapMonths() {
		assertEquals(0,
			TimeUtils.calculateOverlapMonths(
				YearMonth.of(2024, 1),
				YearMonth.of(2024, 1),
				YearMonth.of(2024, 2),
				YearMonth.of(2024, 2)
			)
		);
		assertEquals(0,
			TimeUtils.calculateOverlapMonths(
				YearMonth.of(2024, 1),
				YearMonth.of(2024, 2),
				YearMonth.of(2024, 3),
				YearMonth.of(2024, 4)
			)
		);
		assertEquals(1,
			TimeUtils.calculateOverlapMonths(
				YearMonth.of(2024, 1),
				YearMonth.of(2024, 1),
				YearMonth.of(2024, 1),
				YearMonth.of(2024, 1)
			)
		);
		assertEquals(2,
			TimeUtils.calculateOverlapMonths(
				YearMonth.of(2023, 12),
				YearMonth.of(2024, 1),
				YearMonth.of(2023, 12),
				YearMonth.of(2024, 1)
			)
		);
		assertEquals(2,
			TimeUtils.calculateOverlapMonths(
				YearMonth.of(2023, 12),
				YearMonth.of(2024, 1),
				YearMonth.of(2023, 11),
				YearMonth.of(2024, 2)
			)
		);
		assertEquals(7,
			TimeUtils.calculateOverlapMonths(
				YearMonth.of(2023, 1),
				YearMonth.of(2024, 1),
				YearMonth.of(2023, 7),
				YearMonth.of(2024, 5)
			)
		);
	}

}
