package com.lennartmoeller.finance.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Optional;
import javax.annotation.Nullable;

public final class EuroParser {
    private EuroParser() {}

    public static Optional<Long> parseToCents(@Nullable String text) {
        if (text == null || text.isBlank()) {
            return Optional.empty();
        }
        try {
            Number number = NumberFormat.getNumberInstance(Locale.GERMANY).parse(text.replace("\u00A0", ""));
            return Optional.of(Math.round(number.doubleValue() * 100));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid amount: " + text, e);
        }
    }
}
