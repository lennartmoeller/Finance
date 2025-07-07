package com.lennartmoeller.finance.csv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class EuroParserTest {

    @Test
    void shouldParseAmountIntoCents() {
        Optional<Long> result = EuroParser.parseToCents("1,23");
        assertThat(result).contains(123L);
    }

    @Test
    void shouldHandleThousandSeparatorsAndWhitespace() {
        Optional<Long> result = EuroParser.parseToCents("1\u00A0234,50");
        assertThat(result).contains(123450L);
    }

    @Test
    void shouldReturnEmptyForNullOrBlank() {
        assertThat(EuroParser.parseToCents(null)).isEmpty();
        assertThat(EuroParser.parseToCents("  ")).isEmpty();
    }

    @Test
    void shouldParseNegativeValues() {
        Optional<Long> result = EuroParser.parseToCents("-5,00");
        assertThat(result).contains(-500L);
    }

    @Test
    void shouldThrowForInvalidInput() {
        assertThatThrownBy(() -> EuroParser.parseToCents("abc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid amount");
    }
}
