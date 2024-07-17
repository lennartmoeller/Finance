package com.lennartmoeller.finance.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsServiceTest {

	@Test
	void calculateOverlapDays() {
		assertEquals(0,
			StatisticsService.calculateOverlapDays(
				LocalDate.of(2024, 1, 1),
				LocalDate.of(2024, 1, 1),
				LocalDate.of(2024, 1, 2),
				LocalDate.of(2024, 1, 2)
			)
		);
		assertEquals(0,
			StatisticsService.calculateOverlapDays(
				LocalDate.of(2024, 1, 1),
				LocalDate.of(2024, 1, 2),
				LocalDate.of(2024, 1, 3),
				LocalDate.of(2024, 1, 4)
			)
		);
		assertEquals(1,
			StatisticsService.calculateOverlapDays(
				LocalDate.of(2024, 1, 1),
				LocalDate.of(2024, 1, 1),
				LocalDate.of(2024, 1, 1),
				LocalDate.of(2024, 1, 1)
			)
		);
		assertEquals(7,
			StatisticsService.calculateOverlapDays(
				LocalDate.of(2023, 12, 1),
				LocalDate.of(2024, 1, 31),
				LocalDate.of(2023, 12, 28),
				LocalDate.of(2024, 1, 3)
			)
		);
		assertEquals(16,
			StatisticsService.calculateOverlapDays(
				LocalDate.of(2023, 1, 1),
				LocalDate.of(2023, 12, 20),
				LocalDate.of(2023, 12, 5),
				LocalDate.of(2024, 1, 31)
			)
		);
	}

}
