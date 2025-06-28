package com.lennartmoeller.finance.converter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MapToJsonStringConverterTest {
    private final MapToJsonStringConverter converter = new MapToJsonStringConverter();

    @Test
    void roundTrip() {
        Map<String, String> map = new HashMap<>();
        map.put("a", "b");
        String json = converter.convertToDatabaseColumn(map);
        Map<String, String> result = converter.convertToEntityAttribute(json);
        assertEquals(map, result);
    }

    @Test
    void nullHandling() {
        String json = converter.convertToDatabaseColumn(null);
        assertNull(json);

        Map<String, String> map = converter.convertToEntityAttribute(null);
        assertTrue(map.isEmpty());
    }

    @Test
    void invalidJsonThrows() {
        assertThrows(IllegalArgumentException.class, () -> converter.convertToEntityAttribute("{"));
    }
}
