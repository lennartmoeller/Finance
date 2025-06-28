package com.lennartmoeller.finance.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class CamtV8CsvParserTest {
    @Test
    void parsesAndSanitizesIban() throws Exception {
        String csv =
                "\"Auftragskonto\";\"Buchungstag\";\"Valutadatum\";\"Buchungstext\";\"Verwendungszweck\";\"Glaeubiger ID\";\"Mandatsreferenz\";\"Kundenreferenz (End-to-End)\";\"Sammlerreferenz\";\"Lastschrift Ursprungsbetrag\";\"Auslagenersatz Ruecklastschrift\";\"Beguenstigter/Zahlungspflichtiger\";\"Kontonummer/IBAN\";\"BIC (SWIFT-Code)\";\"Betrag\";\"Waehrung\";\"Info\"\n"
                        + "\"DE12 3456 7890 1234 5678 90\";\"01.01.25\";\"01.01.25\";\"TEXT\";\"purpose\";\"\";\"\";\"\";\"\";\"\";\"\";\"counter\";\"DE87654321\";\"BIC\";\"1,00\";\"EUR\";\"info\"\n";
        CamtV8CsvParser parser = new CamtV8CsvParser();
        List<CamtV8TransactionDTO> list = parser.parse(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
        assertEquals(1, list.size());
        assertEquals("DE12345678901234567890", list.get(0).getIban());
    }

    @Test
    void handlesBom() throws Exception {
        String csv =
                "\uFEFF\"Auftragskonto\";\"Buchungstag\";\"Valutadatum\";\"Buchungstext\";\"Verwendungszweck\";\"Glaeubiger ID\";\"Mandatsreferenz\";\"Kundenreferenz (End-to-End)\";\"Sammlerreferenz\";\"Lastschrift Ursprungsbetrag\";\"Auslagenersatz Ruecklastschrift\";\"Beguenstigter/Zahlungspflichtiger\";\"Kontonummer/IBAN\";\"BIC (SWIFT-Code)\";\"Betrag\";\"Waehrung\";\"Info\"\n"
                        + "\"DE12\";\"01.01.25\";\"01.01.25\";\"T\";\"p\";\"\";\"\";\"\";\"\";\"\";\"\";\"c\";\"DE\";\"B\";\"1,00\";\"EUR\";\"i\"\n";
        CamtV8CsvParser parser = new CamtV8CsvParser();
        List<CamtV8TransactionDTO> list = parser.parse(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
        assertEquals(1, list.size());
    }

    @Test
    void skipsInvalidLine() throws Exception {
        String csv = "\"Auftragskonto\";\"Buchungstag\"\ninvalid";
        CamtV8CsvParser parser = new CamtV8CsvParser();
        List<CamtV8TransactionDTO> list = parser.parse(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
        assertTrue(list.isEmpty());
    }
}
