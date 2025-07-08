package com.lennartmoeller.finance.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.YearMonth;
import javax.annotation.Nullable;

@Converter(autoApply = true)
public class YearMonthConverter implements AttributeConverter<YearMonth, String> {
    @Override
    public @Nullable String convertToDatabaseColumn(@Nullable YearMonth yearMonth) {
        return yearMonth != null ? yearMonth.toString() : null;
    }

    @Override
    public @Nullable YearMonth convertToEntityAttribute(@Nullable String dbData) {
        return dbData != null ? YearMonth.parse(dbData) : null;
    }
}
