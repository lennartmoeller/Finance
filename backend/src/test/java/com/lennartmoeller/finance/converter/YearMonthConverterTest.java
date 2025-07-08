package com.lennartmoeller.finance.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class YearMonthConverterTest {

    private final YearMonthConverter converter = new YearMonthConverter();

    @Nested
    class ConvertToDatabaseColumn {
        @ParameterizedTest
        @CsvSource({"2020-12", "0001-01", "9999-12"})
        void writesStringRepresentation(String value) {
            YearMonth yearMonth = YearMonth.parse(value);
            assertThat(converter.convertToDatabaseColumn(yearMonth)).isEqualTo(value);
        }

        @Test
        void returnsNullForNullInput() {
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
        }
    }

    @Nested
    class ConvertToEntityAttribute {
        @ParameterizedTest
        @CsvSource({"2015-02", "2024-07"})
        void parsesStringToYearMonth(String value) {
            assertThat(converter.convertToEntityAttribute(value)).isEqualTo(YearMonth.parse(value));
        }

        @Test
        void returnsNullForNullInput() {
            assertThat(converter.convertToEntityAttribute(null)).isNull();
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "2024", "invalid"})
        void failsWhenStringIsInvalid(String value) {
            assertThatThrownBy(() -> converter.convertToEntityAttribute(value))
                    .isInstanceOf(DateTimeParseException.class);
        }
    }
}
