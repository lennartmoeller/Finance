package com.lennartmoeller.finance.util;

import lombok.Getter;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

@Getter
public class DateRange {
	private final LocalDate startDate;
	private final LocalDate endDate;

	public DateRange(LocalDate date) {
		this.startDate = date;
		this.endDate = date;
	}

	public DateRange(LocalDate startDate, LocalDate endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public DateRange(YearMonth yearMonth) {
		this.startDate = yearMonth.atDay(1);
		this.endDate = yearMonth.atEndOfMonth();
	}

	public DateRange(YearMonth startDate, YearMonth endDate) {
		this.startDate = startDate.atDay(1);
		this.endDate = endDate.atEndOfMonth();
	}

	public DateRange(YearQuarter yearQuarter) {
		this.startDate = yearQuarter.firstDay();
		this.endDate = yearQuarter.lastDay();
	}

	public DateRange(YearQuarter startDate, YearQuarter endDate) {
		this.startDate = startDate.firstDay();
		this.endDate = endDate.lastDay();
	}

	public DateRange(YearHalf yearHalf) {
		this.startDate = yearHalf.firstDay();
		this.endDate = yearHalf.lastDay();
	}

	public DateRange(YearHalf startDate, YearHalf endDate) {
		this.startDate = startDate.firstDay();
		this.endDate = endDate.lastDay();
	}

	public DateRange(Year year) {
		this.startDate = year.atDay(1);
		this.endDate = year.atMonth(12).atEndOfMonth();
	}

	public DateRange(Year startDate, Year endDate) {
		this.startDate = startDate.atDay(1);
		this.endDate = endDate.atMonth(12).atDay(31);
	}

	public YearMonth getStartMonth() {
		return YearMonth.from(startDate);
	}

	public YearMonth getEndMonth() {
		return YearMonth.from(endDate);
	}

	public long getOverlapDays(DateRange other) {
		LocalDate start1 = this.getStartDate();
		LocalDate end1 = this.getEndDate();
		LocalDate start2 = other.getStartDate();
		LocalDate end2 = other.getEndDate();

		LocalDate overlapStart = start1.isAfter(start2) ? start1 : start2;
		LocalDate overlapEnd = end1.isBefore(end2) ? end1 : end2;

		if (overlapStart.isBefore(overlapEnd) || overlapStart.isEqual(overlapEnd)) {
			return ChronoUnit.DAYS.between(overlapStart, overlapEnd) + 1;
		} else {
			return 0;
		}
	}

	public long getOverlapMonths(DateRange other) {
		YearMonth start1 = getStartMonth();
		YearMonth end1 = getEndMonth();
		YearMonth start2 = YearMonth.from(other.getStartDate());
		YearMonth end2 = YearMonth.from(other.getEndDate());

		YearMonth overlapStart = start1.isAfter(start2) ? start1 : start2;
		YearMonth overlapEnd = end1.isBefore(end2) ? end1 : end2;

		if (overlapStart.isBefore(overlapEnd) || overlapStart.equals(overlapEnd)) {
			return ChronoUnit.MONTHS.between(overlapStart, overlapEnd) + 1;
		} else {
			return 0;
		}
	}

	public Stream<LocalDate> createDateStream() {
		long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
		return Stream.iterate(startDate, date -> date.plusDays(1)).limit(totalDays);
	}

	public Stream<YearMonth> createMonthStream() {
		YearMonth startMonth = getStartMonth();
		YearMonth endMonth = getEndMonth();
		long totalMonths = ChronoUnit.MONTHS.between(startMonth, endMonth) + 1;
		return Stream.iterate(startMonth, month -> month.plusMonths(1)).limit(totalMonths);
	}

}
