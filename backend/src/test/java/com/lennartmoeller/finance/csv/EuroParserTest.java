package com.lennartmoeller.finance.csv;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class EuroParserTest {
    @Test
    void parsesAmount() {
        Optional<Long> amount = EuroParser.parseToCents("1,23");
        assertEquals(Optional.of(123L), amount);
    }

    @Test
    void ignoresWhitespace() {
        Optional<Long> amount = EuroParser.parseToCents("1\u00A0234,50");
        assertEquals(Optional.of(123450L), amount);
    }

    @Test
    void emptyReturnsEmpty() {
        assertTrue(EuroParser.parseToCents(" ").isEmpty());
    }

    @Test
    void invalidThrows() {
        assertThrows(IllegalArgumentException.class, () -> EuroParser.parseToCents("abc"));
    }
}
