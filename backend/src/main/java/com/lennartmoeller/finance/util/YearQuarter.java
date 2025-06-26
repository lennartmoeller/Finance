package com.lennartmoeller.finance.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

	public static YearQuarter from(YearMonth yearMonth) {
		int month = yearMonth.getMonthValue();
		int quarter = (month - 1) / 3 + 1;
		return new YearQuarter(yearMonth.getYear(), quarter);
	}

	public static YearQuarter from(LocalDate date) {
		int month = date.getMonthValue();
		int quarter = (month - 1) / 3 + 1;
		return new YearQuarter(date.getYear(), quarter);
	}

	public static YearQuarter now() {
		LocalDate now = LocalDate.now();
		return from(now);
	}

        public static YearQuarter parse(String text) {
                if (text == null) {
                        throw new NullPointerException("Text cannot be null");
                }
                if (!text.matches("\\d{4}-Q[1-4]")) {
                        throw new DateTimeParseException(
                                "Invalid YearQuarter format", text, 0);
                }
                int year = Integer.parseInt(text.substring(0, 4));
                int quarter = Character.digit(text.charAt(6), 10);
                return new YearQuarter(year, quarter);
        }

	public LocalDate firstDay() {
		Month startMonth = switch (quarter) {
			case 1 -> Month.JANUARY;
			case 2 -> Month.APRIL;
			case 3 -> Month.JULY;
			case 4 -> Month.OCTOBER;
			default -> throw new IllegalArgumentException("Invalid quarter: " + quarter);
		};
		return YearMonth.of(year, startMonth).atDay(1);
	}

	public LocalDate lastDay() {
		Month month = switch (quarter) {
			case 1 -> Month.MARCH;
			case 2 -> Month.JUNE;
			case 3 -> Month.SEPTEMBER;
			case 4 -> Month.DECEMBER;
			default -> throw new IllegalArgumentException("Invalid quarter: " + quarter);
		};
		return YearMonth.of(year, month).atEndOfMonth();
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
