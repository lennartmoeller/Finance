package com.lennartmoeller.finance.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MapToJsonStringConverterTest {

    private final MapToJsonStringConverter converter = new MapToJsonStringConverter();

    @Test
    void shouldRoundTripMap() {
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

    @Test
    void shouldThrowIllegalArgumentExceptionWhenJsonInvalid() {
        assertThatThrownBy(() -> converter.convertToEntityAttribute("{"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot read JSON to map");
    }
}
