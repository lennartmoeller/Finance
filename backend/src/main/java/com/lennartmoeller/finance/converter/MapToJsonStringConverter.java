package com.lennartmoeller.finance.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.springframework.stereotype.Component;

@Converter
@Component
public class MapToJsonStringConverter implements AttributeConverter<Map<String, String>, String> {
    private static final ObjectMapper OBJECT_MAPPER = createCanonicalMapper();

    private static ObjectMapper createCanonicalMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        mapper.disable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }

    @Override
    public @Nullable String convertToDatabaseColumn(@Nullable Map<String, String> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cannot write map to JSON", e);
        }
    }

    @Override
    public Map<String, String> convertToEntityAttribute(@Nullable String dbData) {
        if (dbData == null) {
            return new HashMap<>();
        }
        try {
            return OBJECT_MAPPER.readValue(dbData, new TypeReference<>() {});
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read JSON to map", e);
        }
    }
}
