package com.lennartmoeller.finance.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

public class TimeUtils {

	private TimeUtils() {
	}

	public static long calculateOverlapDays(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
		LocalDate overlapStart = start1.isAfter(start2) ? start1 : start2;
		LocalDate overlapEnd = end1.isBefore(end2) ? end1 : end2;

		if (overlapStart.isBefore(overlapEnd) || overlapStart.isEqual(overlapEnd)) {
			return ChronoUnit.DAYS.between(overlapStart, overlapEnd) + 1;
		} else {
			return 0;
		}
	}

	public static long calculateOverlapMonths(YearMonth start1, YearMonth end1, YearMonth start2, YearMonth end2) {
		YearMonth overlapStart = start1.isAfter(start2) ? start1 : start2;
		YearMonth overlapEnd = end1.isBefore(end2) ? end1 : end2;

		if (overlapStart.isBefore(overlapEnd) || overlapStart.equals(overlapEnd)) {
			return ChronoUnit.MONTHS.between(overlapStart, overlapEnd) + 1;
		} else {
			return 0;
		}
	}

	public static Stream<LocalDate> createDateStream(LocalDate start, LocalDate end) {
		long totalDays = ChronoUnit.DAYS.between(start, end) + 1;
		return Stream.iterate(start, date -> date.plusDays(1)).limit(totalDays);
	}

	public static Stream<YearMonth> createMonthStream(YearMonth start, YearMonth end) {
		long totalMonths = ChronoUnit.MONTHS.between(start, end) + 1;
		return Stream.iterate(start, month -> month.plusMonths(1)).limit(totalMonths);
	}

}
