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
    @CsvSource({"2023-01", "1999-12", "0001-01"})
    void shouldConvertRoundTripForValidValues(String value) {
        YearMonth yearMonth = YearMonth.parse(value);

        String db = converter.convertToDatabaseColumn(yearMonth);
        YearMonth result = converter.convertToEntityAttribute(db);

        assertThat(db).isEqualTo(value);
        assertThat(result).isEqualTo(yearMonth);
    }

    @Test
    void shouldReturnNullForNullInput() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void shouldThrowDateTimeParseExceptionForInvalidString() {
        assertThatThrownBy(() -> converter.convertToEntityAttribute("not-a-date"))
                .isInstanceOf(DateTimeParseException.class);
    }
}
