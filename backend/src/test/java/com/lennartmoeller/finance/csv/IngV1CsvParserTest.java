package com.lennartmoeller.finance.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class IngV1CsvParserTest {
    @Test
    void parsesAndSanitizesIban() throws Exception {
        String csv =
                """
Umsatzanzeige;Datei erstellt am: 27.06.2025 23:40

IBAN;DE12 3456 7890 1234 5678 90
Buchung;Wertstellungsdatum;Auftraggeber;Buchungstext;Verwendungszweck;Saldo;Währung;Betrag;Währung
01.01.2025;01.01.2025;Counter;Text;Purpose;100,00;EUR;5,00;EUR
""";
        IngV1CsvParser parser = new IngV1CsvParser();
        List<IngV1TransactionDTO> list = parser.parse(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
        assertEquals(1, list.size());
        assertEquals("DE12345678901234567890", list.get(0).getIban());
    }

    @Test
    void returnsEmptyWithoutHeader() throws Exception {
        String csv = "IBAN;DE12\nfoo";
        IngV1CsvParser parser = new IngV1CsvParser();
        List<IngV1TransactionDTO> list = parser.parse(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
        assertTrue(list.isEmpty());
    }

    @Test
    void skipsInvalidLine() throws Exception {
        String csv = "IBAN;DE12\nBuchung;Wertstellungsdatum\ninvalid";
        IngV1CsvParser parser = new IngV1CsvParser();
        List<IngV1TransactionDTO> list = parser.parse(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
        assertTrue(list.isEmpty());
    }

    @Test
    void parsesDuplicateHeaders() throws Exception {
        String csv =
                "IBAN;DE12\nBuchung;Wertstellungsdatum;Auftraggeber;Buchungstext;Verwendungszweck;Saldo;Währung;Betrag;Währung;Währung\n01.01.2025;01.01.2025;Counter;Text;Purpose;100,00;EUR;5,00;EUR;EUR";
        IngV1CsvParser parser = new IngV1CsvParser();
        List<IngV1TransactionDTO> list = parser.parse(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
        assertEquals(1, list.size());
        assertTrue(list.get(0).getData().containsKey("Währung"));
        assertTrue(list.get(0).getData().containsKey("Währung_9"));
    }

    @Test
    void skipsBlankLine() throws Exception {
        String csv =
                "IBAN;DE12\nBuchung;Wertstellungsdatum;Auftraggeber;Buchungstext;Verwendungszweck;Saldo;Währung;Betrag;Währung\n\n";
        IngV1CsvParser parser = new IngV1CsvParser();
        List<IngV1TransactionDTO> list = parser.parse(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
        assertTrue(list.isEmpty());
    }

    @Test
    void parseLineDirect() throws Exception {
        IngV1CsvParser parser = new IngV1CsvParser();
        java.lang.reflect.Method m =
                IngV1CsvParser.class.getDeclaredMethod("parseLine", String.class, String[].class, String.class);
        m.setAccessible(true);
        String[] headers = {"Date", "Col"};
        Object r1 = m.invoke(parser, "", headers, "DE");
        assertTrue(((java.util.Optional<?>) r1).isEmpty());
        Object r2 = m.invoke(parser, "01.01.2025;01.01.2025;C;T;P;1,0;EUR;2,0;EUR", headers, "DE");
        assertTrue(((java.util.Optional<?>) r2).isPresent());
    }

    @Test
    void duplicateHeaderUsesLaterValue() throws Exception {
        IngV1CsvParser parser = new IngV1CsvParser();
        java.lang.reflect.Method m =
                IngV1CsvParser.class.getDeclaredMethod("parseLine", String.class, String[].class, String.class);
        m.setAccessible(true);
        String[] headers = {"Buchung", "Buchung"};
        Object result = m.invoke(parser, "01.01.2025;02.01.2025;C;T;P;1,0;EUR;2,0;EUR", headers, "DE");
        IngV1TransactionDTO dto = (IngV1TransactionDTO) ((java.util.Optional<?>) result).orElseThrow();
        assertEquals("01.01.2025", dto.getData().get("Buchung"));
        assertEquals("02.01.2025", dto.getData().get("Buchung_1"));
    }
}
