package com.lennartmoeller.finance.csv;

import static org.junit.jupiter.api.Assertions.*;

import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class CamtV8CsvParserTest {
    @Test
    void parsesAndSanitizesIban() throws Exception {
        String csv =
                """
			"Auftragskonto";"Buchungstag";"Valutadatum";"Buchungstext";"Verwendungszweck";"Glaeubiger ID";"Mandatsreferenz";"Kundenreferenz (End-to-End)";"Sammlerreferenz";"Lastschrift Ursprungsbetrag";"Auslagenersatz Ruecklastschrift";"Beguenstigter/Zahlungspflichtiger";"Kontonummer/IBAN";"BIC (SWIFT-Code)";"Betrag";"Waehrung";"Info"
			"DE12 3456 7890 1234 5678 90";"01.01.25";"01.01.25";"TEXT";"purpose";"";"";"";"";"";"";"counter";"DE87654321";"BIC";"1,00";"EUR";"info"
			""";
        CamtV8CsvParser parser = new CamtV8CsvParser();
        List<CamtV8TransactionDTO> list = parser.parse(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
        assertEquals(1, list.size());
        assertEquals("DE12345678901234567890", list.getFirst().getIban());
    }

    @Test
    void handlesBom() throws Exception {
        String csv =
                """
			\uFEFF"Auftragskonto";"Buchungstag";"Valutadatum";"Buchungstext";"Verwendungszweck";"Glaeubiger ID";"Mandatsreferenz";"Kundenreferenz (End-to-End)";"Sammlerreferenz";"Lastschrift Ursprungsbetrag";"Auslagenersatz Ruecklastschrift";"Beguenstigter/Zahlungspflichtiger";"Kontonummer/IBAN";"BIC (SWIFT-Code)";"Betrag";"Waehrung";"Info"
			"DE12";"01.01.25";"01.01.25";"T";"p";"";"";"";"";"";"";"c";"DE";"B";"1,00";"EUR";"i"
			""";
        CamtV8CsvParser parser = new CamtV8CsvParser();
        List<CamtV8TransactionDTO> list = parser.parse(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
        assertEquals(1, list.size());
    }

    @Test
    void emptyInputReturnsEmpty() throws Exception {
        CamtV8CsvParser parser = new CamtV8CsvParser();
        List<CamtV8TransactionDTO> list = parser.parse(new ByteArrayInputStream(new byte[0]));
        assertTrue(list.isEmpty());
    }

    static List<String> invalidCsvInputs() {
        return List.of(
                """
			"Auftragskonto";"Buchungstag"
			invalid
			""",
                """
			"Auftragskonto";"Buchungstag";"Valutadatum"
			"DE";"01.01.25"
			""",
                """
			"Auftragskonto";"Buchungstag";"Valutadatum";"Buchungstext";"Verwendungszweck";"Glaeubiger ID";"Mandatsreferenz";"Kundenreferenz (End-to-End)";"Sammlerreferenz";"Lastschrift Ursprungsbetrag";"Auslagenersatz Ruecklastschrift";"Beguenstigter/Zahlungspflichtiger";"Kontonummer/IBAN";"BIC (SWIFT-Code)";"Betrag";"Waehrung";"Info"
			"DE";"01.01.25";"01.01.25";"T"
			""",
                """
			"Auftragskonto";"Buchungstag";"Valutadatum";"Buchungstext";"Verwendungszweck";"Glaeubiger ID";"Mandatsreferenz";"Kundenreferenz (End-to-End)";"Sammlerreferenz";"Lastschrift Ursprungsbetrag";"Auslagenersatz Ruecklastschrift";"Beguenstigter/Zahlungspflichtiger";"Kontonummer/IBAN";"BIC (SWIFT-Code)";"Betrag";"Waehrung";"Info"
			
			""");
    }

    @ParameterizedTest
    @MethodSource("invalidCsvInputs")
    void skipsInvalidOrShortOrBlankLines(String csv) throws Exception {
        CamtV8CsvParser parser = new CamtV8CsvParser();
        List<CamtV8TransactionDTO> list = parser.parse(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
        assertTrue(list.isEmpty());
    }

    @Test
    void parseLineDirect() throws Exception {
        CamtV8CsvParser parser = new CamtV8CsvParser();
        java.lang.reflect.Method m = CamtV8CsvParser.class.getDeclaredMethod("parseLine", String.class);
        m.setAccessible(true);
        assertEquals("A", ((String[]) m.invoke(parser, "\"A\""))[0]);
        assertEquals("B", ((String[]) m.invoke(parser, "B"))[0]);
    }
}
