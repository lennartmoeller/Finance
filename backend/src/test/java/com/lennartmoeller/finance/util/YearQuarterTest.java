package com.lennartmoeller.finance.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class YearQuarterTest {

	@Test
	void testValidQuarter() {
		YearQuarter q = new YearQuarter(2021, 2);
		assertEquals(LocalDate.of(2021, 4, 1), q.firstDay());
		assertEquals(LocalDate.of(2021, 6, 30), q.lastDay());
	}

	@Test
	void testInvalidQuarter() {
		// Test lower and upper bounds for quarter validity.
		assertThrows(IllegalArgumentException.class, () -> new YearQuarter(2021, 0));
		assertThrows(IllegalArgumentException.class, () -> new YearQuarter(2021, 5));
	}

	@Test
	void testFromYearMonth() {
		YearMonth ym = YearMonth.of(2021, 2);
		YearQuarter q = YearQuarter.from(ym);
		// February falls in Q1.
		assertEquals(1, q.getQuarter());
		assertEquals(2021, q.getYear());
	}

	@Test
	void testFromLocalDate() {
		LocalDate date = LocalDate.of(2021, 8, 15);
		YearQuarter q = YearQuarter.from(date);
		// August falls in Q3.
		assertEquals(3, q.getQuarter());
		assertEquals(2021, q.getYear());
	}

	@Test
	void testFromLocalDateBoundaries() {
		// Verify boundary dates for each quarter.
		// Q1: Jan 1 to Mar 31
		YearQuarter q1Start = YearQuarter.from(LocalDate.of(2021, 1, 1));
		YearQuarter q1End = YearQuarter.from(LocalDate.of(2021, 3, 31));
		assertEquals(1, q1Start.getQuarter());
		assertEquals(1, q1End.getQuarter());

		// Q2: Apr 1 to Jun 30
		YearQuarter q2Start = YearQuarter.from(LocalDate.of(2021, 4, 1));
		YearQuarter q2End = YearQuarter.from(LocalDate.of(2021, 6, 30));
		assertEquals(2, q2Start.getQuarter());
		assertEquals(2, q2End.getQuarter());

		// Q3: Jul 1 to Sep 30
		YearQuarter q3Start = YearQuarter.from(LocalDate.of(2021, 7, 1));
		YearQuarter q3End = YearQuarter.from(LocalDate.of(2021, 9, 30));
		assertEquals(3, q3Start.getQuarter());
		assertEquals(3, q3End.getQuarter());

		// Q4: Oct 1 to Dec 31
		YearQuarter q4Start = YearQuarter.from(LocalDate.of(2021, 10, 1));
		YearQuarter q4End = YearQuarter.from(LocalDate.of(2021, 12, 31));
		assertEquals(4, q4Start.getQuarter());
		assertEquals(4, q4End.getQuarter());
	}

	@Test
	void testNow() {
		// Verify that now() and from(LocalDate.now()) yield the same result.
		YearQuarter nowFromMethod = YearQuarter.now();
		YearQuarter nowFromLocalDate = YearQuarter.from(LocalDate.now());
		assertEquals(nowFromLocalDate, nowFromMethod);
	}

	@Test
	void testParseValid() {
		YearQuarter q = YearQuarter.parse("2021-Q3");
		assertEquals(2021, q.getYear());
		assertEquals(3, q.getQuarter());
	}

	@Test
	void testParseInvalid() {
		// Expect DateTimeParseException for an invalid date format.
		assertThrows(DateTimeParseException.class, () -> YearQuarter.parse("2021/03/31"));
		// Also, a string with an invalid quarter designator should fail parsing.
		assertThrows(DateTimeParseException.class, () -> YearQuarter.parse("2021-Q5"));
	}

	@Test
	void testToStringConsistency() {
		// Ensure that toString and parse are consistent.
		YearQuarter q = new YearQuarter(2021, 4);
		String str = q.toString();
		YearQuarter parsed = YearQuarter.parse(str);
		assertEquals(q, parsed);
	}

	@Test
	void testCompareTo() {
		YearQuarter q1 = new YearQuarter(2021, 1);
		YearQuarter q2 = new YearQuarter(2021, 2);
		assertTrue(q1.compareTo(q2) < 0);
		assertTrue(q2.compareTo(q1) > 0);
		YearQuarter q3 = new YearQuarter(2021, 1);
		assertEquals(0, q1.compareTo(q3));
	}

	@Test
	void testCompareDifferentYears() {
		YearQuarter q1 = new YearQuarter(2020, 4);
		YearQuarter q2 = new YearQuarter(2021, 1);
		assertTrue(q1.compareTo(q2) < 0);
		assertTrue(q2.compareTo(q1) > 0);
	}

	@Test
	void testEqualsAndHashCode() {
		YearQuarter q1 = new YearQuarter(2021, 2);
		YearQuarter q2 = new YearQuarter(2021, 2);
		YearQuarter q3 = new YearQuarter(2021, 3);
		assertEquals(q1, q2);
		assertEquals(q1.hashCode(), q2.hashCode());
		assertNotEquals(q1, q3);
	}

	@ParameterizedTest
	@CsvSource({"2021, 1, 2021-01-01, 2021-03-31", "2021, 2, 2021-04-01, 2021-06-30", "2021, 3, 2021-07-01, 2021-09-30", "2021, 4, 2021-10-01, 2021-12-31"})
	void testFirstAndLastDay(int year, int quarter, String expectedFirst, String expectedLast) {
		YearQuarter q = new YearQuarter(year, quarter);
		assertEquals(LocalDate.parse(expectedFirst), q.firstDay());
		assertEquals(LocalDate.parse(expectedLast), q.lastDay());
	}

}
