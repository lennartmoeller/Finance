package com.lennartmoeller.finance.util;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class YearHalfTest {

    @ParameterizedTest
    @CsvSource({
        "2021, 1, 2021-01-01, 2021-06-30",
        "2021, 2, 2021-07-01, 2021-12-31",
        "2020, 1, 2020-01-01, 2020-06-30",
        "2020, 2, 2020-07-01, 2020-12-31"
    })
    void testFirstAndLastDay(int year, int half, String expectedFirst, String expectedLast) {
        YearHalf yh = new YearHalf(year, half);
        assertEquals(LocalDate.parse(expectedFirst), yh.firstDay());
        assertEquals(LocalDate.parse(expectedLast), yh.lastDay());
    }

    @Test
    void testInvalidHalf() {
        // Test invalid half values: zero and greater than 2.
        assertThrows(IllegalArgumentException.class, () -> new YearHalf(2021, 0));
        assertThrows(IllegalArgumentException.class, () -> new YearHalf(2021, 3));
    }

    @ParameterizedTest
    @CsvSource({"5, 1", "6, 1", "7, 2", "12, 2"})
    void testFromYearMonth(int month, int expectedHalf) {
        YearMonth ym = YearMonth.of(2021, month);
        YearHalf yh = YearHalf.from(ym);
        assertEquals(2021, yh.getYear());
        assertEquals(expectedHalf, yh.getHalf());
    }

    @ParameterizedTest
    @CsvSource({"2021-04-15, 1", "2021-06-30, 1", "2021-07-01, 2", "2021-11-15, 2"})
    void testFromLocalDate(String dateStr, int expectedHalf) {
        LocalDate date = LocalDate.parse(dateStr);
        YearHalf yh = YearHalf.from(date);
        assertEquals(date.getYear(), yh.getYear());
        assertEquals(expectedHalf, yh.getHalf());
    }

    @Test
    void testNow() {
        // Ensure that YearHalf.now() matches YearHalf.from(LocalDate.now())
        YearHalf nowFromMethod = YearHalf.now();
        YearHalf nowFromDate = YearHalf.from(LocalDate.now());
        assertEquals(nowFromDate, nowFromMethod);
    }

    @Test
    void testParseValid() {
        YearHalf yh = YearHalf.parse("2021-H2");
        assertEquals(2021, yh.getYear());
        assertEquals(2, yh.getHalf());
    }

    @Test
    void testParseInvalid() {
        // Expect a DateTimeParseException for strings that don't match the expected format.
        assertThrows(DateTimeParseException.class, () -> YearHalf.parse("2021/07/01"));
        // Also, a string with an invalid half (e.g. "H3") should fail.
        assertThrows(DateTimeParseException.class, () -> YearHalf.parse("2021-H3"));
        assertThrows(NullPointerException.class, () -> YearHalf.parse(null));
    }

    @Test
    void testToStringConsistency() {
        // Ensure that converting to a string and parsing it back yields an equal object.
        YearHalf original = new YearHalf(2021, 2);
        String str = original.toString();
        YearHalf parsed = YearHalf.parse(str);
        assertEquals(original, parsed);
    }

    @Test
    void testCompareTo() {
        YearHalf firstHalf = new YearHalf(2021, 1);
        YearHalf secondHalf = new YearHalf(2021, 2);
        assertTrue(firstHalf.compareTo(secondHalf) < 0);
        assertTrue(secondHalf.compareTo(firstHalf) > 0);
        YearHalf anotherFirstHalf = new YearHalf(2021, 1);
        assertEquals(0, firstHalf.compareTo(anotherFirstHalf));
    }

    @Test
    void testCompareDifferentYears() {
        YearHalf previousYearSecondHalf = new YearHalf(2020, 2);
        YearHalf currentYearFirstHalf = new YearHalf(2021, 1);
        assertTrue(previousYearSecondHalf.compareTo(currentYearFirstHalf) < 0);
        assertTrue(currentYearFirstHalf.compareTo(previousYearSecondHalf) > 0);
    }

    @Test
    void testEqualsAndHashCode() {
        YearHalf h1 = new YearHalf(2021, 1);
        YearHalf h2 = new YearHalf(2021, 1);
        YearHalf h3 = new YearHalf(2021, 2);
        assertEquals(h1, h2);
        assertEquals(h1.hashCode(), h2.hashCode());
        assertNotEquals(h1, h3);
    }

    @Test
    void testInvalidFirstDayViaReflection() throws Exception {
        YearHalf h = new YearHalf(2021, 1);
        Field f = YearHalf.class.getDeclaredField("half");
        f.setAccessible(true);
        f.setInt(h, 3);
        assertThrows(IllegalArgumentException.class, h::firstDay);
        assertThrows(IllegalArgumentException.class, h::lastDay);
    }
}
