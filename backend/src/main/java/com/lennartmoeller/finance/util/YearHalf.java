package com.lennartmoeller.finance.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

@Getter
@EqualsAndHashCode
public final class YearHalf implements Comparable<YearHalf> {

	private final int year;
	private final int half;

	public YearHalf(int year, int half) {
		if (half < 1 || half > 2) {
			throw new IllegalArgumentException("Half must be 1 or 2");
		}
		this.year = year;
		this.half = half;
	}

	public static YearHalf from(YearMonth yearMonth) {
		int month = yearMonth.getMonthValue();
		int half = month <= 6 ? 1 : 2;
		return new YearHalf(yearMonth.getYear(), half);
	}

	public static YearHalf from(LocalDate date) {
		int month = date.getMonthValue();
		int half = month <= 6 ? 1 : 2;
		return new YearHalf(date.getYear(), half);
	}

	public static YearHalf now() {
		LocalDate now = LocalDate.now();
		return from(now);
	}

	public static YearHalf parse(String text) {
		if (text == null) {
			throw new NullPointerException("Text cannot be null");
		}
		if (!text.matches("\\d{4}-H[12]")) {
			throw new DateTimeParseException(
				"Invalid YearHalf format", text, 0);
		}
		int year = Integer.parseInt(text.substring(0, 4));
		int half = Character.digit(text.charAt(6), 10);
		return new YearHalf(year, half);
	}

	public LocalDate firstDay() {
		Month startMonth = switch (half) {
			case 1 -> Month.JANUARY;
			case 2 -> Month.JULY;
			default -> throw new IllegalArgumentException("Invalid half: " + half);
		};
		return YearMonth.of(year, startMonth).atDay(1);
	}

	public LocalDate lastDay() {
		Month month = switch (half) {
			case 1 -> Month.JUNE;
			case 2 -> Month.DECEMBER;
			default -> throw new IllegalArgumentException("Invalid half: " + half);
		};
		return YearMonth.of(year, month).atEndOfMonth();
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
