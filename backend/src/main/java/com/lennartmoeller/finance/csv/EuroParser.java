package com.lennartmoeller.finance.csv;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public final class EuroParser {
    private EuroParser() {}

    public static Long parseToCents(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            Number number = NumberFormat.getNumberInstance(Locale.GERMANY).parse(text.replace("\u00A0", ""));
            return Math.round(number.doubleValue() * 100);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid amount: " + text, e);
        }
    }
}
