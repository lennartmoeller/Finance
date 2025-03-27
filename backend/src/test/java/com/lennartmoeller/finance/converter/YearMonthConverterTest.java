package com.lennartmoeller.finance.converter;

import org.junit.jupiter.api.Test;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

class YearMonthConverterTest {

    private final YearMonthConverter converter = new YearMonthConverter();

    @Test
    void testConvertToDatabaseColumn_withNonNullValue() {
        YearMonth yearMonth = YearMonth.of(2023, 5);
        String expected = "2023-05";
        String actual = converter.convertToDatabaseColumn(yearMonth);
        assertEquals(expected, actual, "Converter should correctly convert YearMonth to its String representation");
    }

    @Test
    void testConvertToDatabaseColumn_withNullValue() {
        assertNull(converter.convertToDatabaseColumn(null), "Converter should return null when provided a null YearMonth");
    }

    @Test
    void testConvertToEntityAttribute_withNonNullValue() {
        String dbData = "2023-05";
        YearMonth expected = YearMonth.of(2023, 5);
        YearMonth actual = converter.convertToEntityAttribute(dbData);
        assertEquals(expected, actual, "Converter should correctly parse the String to a YearMonth");
    }

    @Test
    void testConvertToEntityAttribute_withNullValue() {
        assertNull(converter.convertToEntityAttribute(null), "Converter should return null when provided a null String");
    }

}
