package com.lennartmoeller.finance.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MapToJsonStringConverterTest {

    private final MapToJsonStringConverter converter = new MapToJsonStringConverter();

    @Test
    void shouldConvertMapToJsonAndBack() {
        Map<String, String> input = new LinkedHashMap<>();
        input.put("a", "b");
        input.put("c", "d");

        String json = converter.convertToDatabaseColumn(input);
        Map<String, String> result = converter.convertToEntityAttribute(json);

        assertThat(result).containsExactlyEntriesOf(input);
    }

    @Test
    void shouldReturnNullWhenMapIsNull() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    void shouldReturnEmptyMapWhenJsonIsNull() {
        assertThat(converter.convertToEntityAttribute(null)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"{", ""})
    void shouldThrowIllegalArgumentExceptionWhenJsonInvalid(String json) {
        assertThatThrownBy(() -> converter.convertToEntityAttribute(json))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot read JSON to map");
    }
}
