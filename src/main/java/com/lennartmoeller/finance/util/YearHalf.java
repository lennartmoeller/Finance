package com.lennartmoeller.finance.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@EqualsAndHashCode
public final class YearHalf implements Comparable<YearHalf> {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-'H'H");

	private final int year;
	private final int half;

	public YearHalf(int year, int half) {
		if (half < 1 || half > 2) {
			throw new IllegalArgumentException("Half must be 1 or 2");
		}
		this.year = year;
		this.half = half;
	}

	public static YearHalf from(LocalDate date) {
		int year = date.getYear();
		int month = date.getMonthValue();
		int half = (month - 1) / 6 + 1;
		return new YearHalf(year, half);
	}

	public static YearHalf now() {
		LocalDate now = LocalDate.now();
		return from(now);
	}

	public static YearHalf parse(String text) {
		LocalDate date = LocalDate.parse(text, FORMATTER);
		return from(date);
	}

	public LocalDate atDay(int dayOfHalf) {
		LocalDate startOfHalf = LocalDate.of(year, (half - 1) * 6 + 1, 1);
		return startOfHalf.plusDays(dayOfHalf - (long) 1);
	}

	public LocalDate endOfHalfYear() {
		LocalDate startOfNextHalf = LocalDate.of(year, half * 6 + 1, 1);
		return startOfNextHalf.minusDays(1);
	}

	@Override
	public int compareTo(YearHalf other) {
		int cmp = Integer.compare(this.year, other.year);
		if (cmp == 0) {
			cmp = Integer.compare(this.half, other.half);
		}
		return cmp;
	}

	@Override
	public String toString() {
		return String.format("%d-H%d", year, half);
	}

}
