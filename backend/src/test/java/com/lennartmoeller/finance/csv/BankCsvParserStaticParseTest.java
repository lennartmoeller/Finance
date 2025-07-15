import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lennartmoeller.finance.csv.BankCsvParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class BankCsvParserStaticParseTest {
    @Test
    void failsOnShortCamtFile() throws IOException {
        String csv =
                "\"Auftragskonto\";\"Buchungstag\";\"Valutadatum\";\"Buchungstext\";\"Verwendungszweck\";\"Glaeubiger ID\";\"Mandatsreferenz\";\"Kundenreferenz(End-to-End)\";\"Sammlerreferenz\";\"Lastschrift Ursprungsbetrag\";\"Auslagenersatz Ruecklastschrift\";\"Beguenstigter/Zahlungspflichtiger\";\"Kontonummer/IBAN\";\"BIC (SWIFT-Code)\";\"Betrag\";\"Waehrung\";\"Info\"\n"
                        + "DE12;01.01.24;01.01.24;Text;Purpose;CID;MID;CR;Collector;O;F;Counter;DE12;BIC;1,00;EUR;Info";
        MockMultipartFile file = new MockMultipartFile("f", csv.getBytes(StandardCharsets.UTF_8));
        assertThatThrownBy(() -> BankCsvParser.parse(file, Map.of())).isInstanceOf(IndexOutOfBoundsException.class);
    }
}
