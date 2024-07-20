package com.lennartmoeller.finance.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@EqualsAndHashCode
public final class YearQuarter implements Comparable<YearQuarter> {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-'Q'Q");

	private final int year;
	private final int quarter;

	public YearQuarter(int year, int quarter) {
		if (quarter < 1 || quarter > 4) {
			throw new IllegalArgumentException("Quarter must be between 1 and 4");
		}
		this.year = year;
		this.quarter = quarter;
	}

	public static YearQuarter from(LocalDate date) {
		int year = date.getYear();
		int month = date.getMonthValue();
		int quarter = (month - 1) / 3 + 1;
		return new YearQuarter(year, quarter);
	}

	public static YearQuarter now() {
		LocalDate now = LocalDate.now();
		return from(now);
	}

	public static YearQuarter parse(String text) {
		LocalDate date = LocalDate.parse(text, FORMATTER);
		return from(date);
	}

	public LocalDate atDay(int dayOfQuarter) {
		LocalDate startOfQuarter = LocalDate.of(year, (quarter - 1) * 3 + 1, 1);
		return startOfQuarter.plusDays(dayOfQuarter - (long) 1);
	}

	public LocalDate endOfQuarterYear() {
		LocalDate startOfNextQuarter = LocalDate.of(year, quarter * 3 + 1, 1);
		return startOfNextQuarter.minusDays(1);
	}

	@Override
	public int compareTo(YearQuarter other) {
		int cmp = Integer.compare(this.year, other.year);
		if (cmp == 0) {
			cmp = Integer.compare(this.quarter, other.quarter);
		}
		return cmp;
	}

	@Override
	public String toString() {
		return String.format("%d-Q%d", year, quarter);
	}

}
