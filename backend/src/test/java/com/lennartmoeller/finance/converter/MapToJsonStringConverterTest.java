package com.lennartmoeller.finance.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MapToJsonStringConverterTest {

    private final MapToJsonStringConverter converter = new MapToJsonStringConverter();

    @Nested
    class ConvertToDatabaseColumn {
        @Test
        void returnsNullForNullInput() {
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
        }

        @Test
        @SuppressWarnings("unchecked")
        void wrapsJsonProcessingException() {
            class SelfRef {
                @SuppressWarnings("unused")
                final SelfRef self = this;
            }

            Map<String, String> map = new HashMap<>();
            @SuppressWarnings("rawtypes")
            Map raw = map; // intentionally bypass generic type check
            raw.put("ref", new SelfRef());

            assertThatThrownBy(() -> converter.convertToDatabaseColumn(map))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cannot write map to JSON");
        }

        @Test
        void serializesMapToJsonString() {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("a", "b");
            map.put("c", "d");

            String json = converter.convertToDatabaseColumn(map);

            assertThat(json).isEqualTo("{\"a\":\"b\",\"c\":\"d\"}");
        }
    }

    @Nested
    class ConvertToEntityAttribute {
        @Test
        void returnsEmptyMapForNullInput() {
            assertThat(converter.convertToEntityAttribute(null)).isEmpty();
        }

        @Test
        void parsesJsonIntoMap() {
            Map<String, String> result = converter.convertToEntityAttribute("{\"one\":\"1\",\"two\":\"2\"}");

            assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of("one", "1", "two", "2"));
        }

        @Test
        void throwsIllegalArgumentExceptionForInvalidJson() {
            assertThatThrownBy(() -> converter.convertToEntityAttribute("{"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cannot read JSON to map");
        }
    }
}
