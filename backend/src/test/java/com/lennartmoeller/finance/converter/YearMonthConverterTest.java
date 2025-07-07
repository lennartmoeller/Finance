package com.lennartmoeller.finance.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class YearMonthConverterTest {

    private final YearMonthConverter converter = new YearMonthConverter();

    @ParameterizedTest
    @CsvSource({"2023-01", "1999-12"})
    void shouldConvertToDatabaseColumnAndBack(String value) {
        YearMonth yearMonth = YearMonth.parse(value);
        String db = converter.convertToDatabaseColumn(yearMonth);
        assertThat(db).isEqualTo(value);
        assertThat(converter.convertToEntityAttribute(db)).isEqualTo(yearMonth);
    }

    @Test
    void shouldReturnNullOnNullInput() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void shouldThrowExceptionForInvalidString() {
        assertThatThrownBy(() -> converter.convertToEntityAttribute("invalid"))
                .isInstanceOf(DateTimeParseException.class);
    }
}
