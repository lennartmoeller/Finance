package com.lennartmoeller.finance.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
