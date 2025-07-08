package com.lennartmoeller.finance.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DateRangeTest {
    // --- Constructors ---

    @Test
    void testSingleDateConstructor() {
        LocalDate date = LocalDate.of(2021, 1, 1);
        DateRange range = new DateRange(date);
        assertEquals(date, range.getStartDate());
        assertEquals(date, range.getEndDate());
        assertEquals(1, range.getDays());
    }

    @Test
    void testDateRangeConstructor() {
        LocalDate start = LocalDate.of(2021, 1, 1);
        LocalDate end = LocalDate.of(2021, 1, 10);
        DateRange range = new DateRange(start, end);
        assertEquals(start, range.getStartDate());
        assertEquals(end, range.getEndDate());
        assertEquals(10, range.getDays());
    }

    @Test
    void testYearMonthConstructor() {
        YearMonth ym = YearMonth.of(2021, 1);
        DateRange range = new DateRange(ym);
        assertEquals(ym.atDay(1), range.getStartDate());
        assertEquals(ym.atEndOfMonth(), range.getEndDate());
        assertEquals(ym.lengthOfMonth(), range.getDays());
    }

    @Test
    void testYearMonthRangeConstructor() {
        YearMonth start = YearMonth.of(2021, 2);
        YearMonth end = YearMonth.of(2021, 4);
        DateRange range = new DateRange(start, end);
        assertEquals(start.atDay(1), range.getStartDate());
        assertEquals(end.atEndOfMonth(), range.getEndDate());
    }

    @Test
    void testYearQuarterConstructor() {
        YearQuarter quarter = new YearQuarter(2021, 2); // Q2: Apr 1 - Jun 30
        DateRange range = new DateRange(quarter);
        assertEquals(quarter.firstDay(), range.getStartDate());
        assertEquals(quarter.lastDay(), range.getEndDate());
    }

    @Test
    void testYearQuarterRangeConstructor() {
        YearQuarter startQuarter = new YearQuarter(2021, 1); // Q1: Jan 1 - Mar 31
        YearQuarter endQuarter = new YearQuarter(2021, 3); // Q3: Jul 1 - Sep 30
        DateRange range = new DateRange(startQuarter, endQuarter);
        assertEquals(startQuarter.firstDay(), range.getStartDate());
        assertEquals(endQuarter.lastDay(), range.getEndDate());
    }

    @Test
    void testYearHalfConstructor() {
        YearHalf half = new YearHalf(2021, 1); // H1: Jan 1 - Jun 30
        DateRange range = new DateRange(half);
        assertEquals(half.firstDay(), range.getStartDate());
        assertEquals(half.lastDay(), range.getEndDate());
    }

    @Test
    void testYearHalfRangeConstructor() {
        YearHalf startHalf = new YearHalf(2021, 1); // H1: Jan 1 - Jun 30
        YearHalf endHalf = new YearHalf(2021, 2); // H2: Jul 1 - Dec 31
        DateRange range = new DateRange(startHalf, endHalf);
        assertEquals(startHalf.firstDay(), range.getStartDate());
        assertEquals(endHalf.lastDay(), range.getEndDate());
    }

    @Test
    void testYearConstructor() {
        Year year = Year.of(2021);
        DateRange range = new DateRange(year);
        assertEquals(year.atDay(1), range.getStartDate());
        assertEquals(year.atMonth(12).atEndOfMonth(), range.getEndDate());
        // For non-leap year 2021, 365 days.
        assertEquals(365, range.getDays());
    }

    @Test
    void testYearRangeConstructor() {
        Year startYear = Year.of(2020);
        Year endYear = Year.of(2021);
        DateRange range = new DateRange(startYear, endYear);
        assertEquals(startYear.atDay(1), range.getStartDate());
        assertEquals(endYear.atMonth(12).atDay(31), range.getEndDate());
        // 2020 (leap year, 366 days) + 2021 (365 days) = 731 days total.
        assertEquals(731, range.getDays());
    }

    // --- Overlap Methods ---

    @Nested
    class OverlapRangeTests {
        @ParameterizedTest
        @MethodSource("com.lennartmoeller.finance.util.DateRangeTest#overlapRangeProvider")
        void verifyOverlapRange(DateRange r1, DateRange r2, LocalDate expectedStart, LocalDate expectedEnd) {
            DateRange result = DateRange.getOverlapRange(r1, r2);

            assertEquals(expectedStart, result.getStartDate());
            assertEquals(expectedEnd, result.getEndDate());
        }
    }

    @Nested
    class OverlapDaysTests {
        @ParameterizedTest
        @MethodSource("com.lennartmoeller.finance.util.DateRangeTest#overlapDaysProvider")
        void verifyOverlapDays(DateRange r1, DateRange r2, long expected) {
            assertEquals(expected, r1.getOverlapDays(r2));
        }
    }

    @Nested
    class OverlapMonthsTests {
        @ParameterizedTest
        @MethodSource("com.lennartmoeller.finance.util.DateRangeTest#overlapMonthsProvider")
        void verifyOverlapMonths(DateRange r1, DateRange r2, long expected) {
            assertEquals(expected, r1.getOverlapMonths(r2));
        }
    }

    // --- Month/Day Calculations ---

    @Test
    void testGetMonths() {
        DateRange range = new DateRange(YearMonth.of(2021, 1), YearMonth.of(2021, 3));
        assertEquals(3, range.getMonths());
    }

    @Test
    void testGetStartAndEndMonth() {
        LocalDate start = LocalDate.of(2021, 2, 15);
        LocalDate end = LocalDate.of(2021, 5, 20);
        DateRange range = new DateRange(start, end);
        assertEquals(YearMonth.of(2021, 2), range.getStartMonth());
        assertEquals(YearMonth.of(2021, 5), range.getEndMonth());
    }

    // --- Stream Methods ---

    @Test
    void testCreateDateStream() {
        LocalDate start = LocalDate.of(2021, 1, 1);
        LocalDate end = LocalDate.of(2021, 1, 3);
        DateRange range = new DateRange(start, end);
        List<LocalDate> dates = range.createDateStream().toList();
        assertEquals(3, dates.size());
        assertEquals(start, dates.get(0));
        assertEquals(LocalDate.of(2021, 1, 2), dates.get(1));
        assertEquals(end, dates.get(2));
    }

    @Test
    void testCreateMonthStream() {
        YearMonth start = YearMonth.of(2021, 1);
        YearMonth end = YearMonth.of(2021, 3);
        DateRange range = new DateRange(start, end);
        List<YearMonth> months = range.createMonthStream().toList();
        assertEquals(3, months.size());
        assertEquals(start, months.get(0));
        assertEquals(YearMonth.of(2021, 2), months.get(1));
        assertEquals(end, months.get(2));
    }

    // --- Edge Cases ---

    @Test
    void testNegativeDateRange() {
        // When startDate is after endDate, getDays() will be negative.
        LocalDate start = LocalDate.of(2021, 1, 10);
        LocalDate end = LocalDate.of(2021, 1, 5);
        DateRange range = new DateRange(start, end);
        assertEquals(-4, range.getDays());
    }

    static Stream<Arguments> overlapRangeProvider() {
        return Stream.of(
                Arguments.of(
                        new DateRange(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 10)),
                        new DateRange(LocalDate.of(2021, 1, 5), LocalDate.of(2021, 1, 15)),
                        LocalDate.of(2021, 1, 5),
                        LocalDate.of(2021, 1, 10)),
                Arguments.of(
                        new DateRange(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 5)),
                        new DateRange(LocalDate.of(2021, 1, 10), LocalDate.of(2021, 1, 15)),
                        LocalDate.of(2021, 1, 5),
                        LocalDate.of(2021, 1, 5)),
                Arguments.of(
                        new DateRange(LocalDate.of(2021, 1, 5), LocalDate.of(2021, 1, 15)),
                        new DateRange(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 10)),
                        LocalDate.of(2021, 1, 5),
                        LocalDate.of(2021, 1, 10)),
                Arguments.of(
                        new DateRange(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 5)),
                        new DateRange(LocalDate.of(2021, 1, 5), LocalDate.of(2021, 1, 10)),
                        LocalDate.of(2021, 1, 5),
                        LocalDate.of(2021, 1, 5)));
    }

    static Stream<Arguments> overlapDaysProvider() {
        return Stream.of(
                Arguments.of(
                        new DateRange(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 10)),
                        new DateRange(LocalDate.of(2021, 1, 5), LocalDate.of(2021, 1, 15)),
                        6L),
                Arguments.of(
                        new DateRange(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 5)),
                        new DateRange(LocalDate.of(2021, 2, 1), LocalDate.of(2021, 2, 5)),
                        0L),
                Arguments.of(
                        new DateRange(LocalDate.of(2021, 1, 5), LocalDate.of(2021, 1, 5)),
                        new DateRange(LocalDate.of(2021, 1, 5), LocalDate.of(2021, 1, 10)),
                        1L));
    }

    static Stream<Arguments> overlapMonthsProvider() {
        return Stream.of(
                Arguments.of(
                        new DateRange(YearMonth.of(2021, 1), YearMonth.of(2021, 4)),
                        new DateRange(YearMonth.of(2021, 3), YearMonth.of(2021, 6)),
                        2L),
                Arguments.of(
                        new DateRange(YearMonth.of(2021, 1), YearMonth.of(2021, 3)),
                        new DateRange(YearMonth.of(2021, 6), YearMonth.of(2021, 8)),
                        0L),
                Arguments.of(
                        new DateRange(YearMonth.of(2021, 3), YearMonth.of(2021, 3)),
                        new DateRange(YearMonth.of(2021, 3), YearMonth.of(2021, 5)),
                        1L),
                Arguments.of(
                        new DateRange(YearMonth.of(2021, 6), YearMonth.of(2021, 8)),
                        new DateRange(YearMonth.of(2021, 1), YearMonth.of(2021, 7)),
                        2L));
    }
}
