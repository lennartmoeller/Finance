package com.lennartmoeller.finance.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class YearMonthConverterTest {

    private final YearMonthConverter converter = new YearMonthConverter();

    @Nested
    class Convert_to_database_column {
        @ParameterizedTest
        @CsvSource({"2020-12", "0001-01", "9999-12"})
        void writes_string_representation(String value) {
            YearMonth yearMonth = YearMonth.parse(value);
            assertThat(converter.convertToDatabaseColumn(yearMonth)).isEqualTo(value);
        }

        @Test
        void returns_null_for_null_input() {
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
        }
    }

    @Nested
    class Convert_to_entity_attribute {
        @ParameterizedTest
        @CsvSource({"2015-02", "2024-07"})
        void parses_string_to_year_month(String value) {
            assertThat(converter.convertToEntityAttribute(value)).isEqualTo(YearMonth.parse(value));
        }

        @Test
        void returns_null_for_null_input() {
            assertThat(converter.convertToEntityAttribute(null)).isNull();
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "2024", "invalid"})
        void fails_when_string_is_invalid(String value) {
            assertThatThrownBy(() -> converter.convertToEntityAttribute(value))
                    .isInstanceOf(DateTimeParseException.class);
        }
    }
}
