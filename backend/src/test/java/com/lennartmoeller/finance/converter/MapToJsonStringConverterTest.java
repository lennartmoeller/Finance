package com.lennartmoeller.finance.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayNameGeneration(ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class MapToJsonStringConverterTest {

    private final MapToJsonStringConverter converter = new MapToJsonStringConverter();

    @Nested
    class Convert_to_database_column {
        @Test
        void returns_null_for_null_input() {
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
        }

        @Test
        void serializes_map_to_json_string() {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("a", "b");
            map.put("c", "d");

            String json = converter.convertToDatabaseColumn(map);

            assertThat(json).isEqualTo("{\"a\":\"b\",\"c\":\"d\"}");
        }
    }

    @Nested
    class Convert_to_entity_attribute {
        @Test
        void returns_empty_map_for_null_input() {
            assertThat(converter.convertToEntityAttribute(null)).isEmpty();
        }

        @Test
        void parses_json_into_map() {
            Map<String, String> result = converter.convertToEntityAttribute("{\"one\":\"1\",\"two\":\"2\"}");

            assertThat(result).containsExactlyEntriesOf(Map.of("one", "1", "two", "2"));
        }

        @Test
        void throws_IllegalArgumentException_for_invalid_json() {
            assertThatThrownBy(() -> converter.convertToEntityAttribute("{"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cannot read JSON to map");
        }
    }
}
