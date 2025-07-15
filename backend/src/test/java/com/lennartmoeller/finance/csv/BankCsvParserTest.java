package com.lennartmoeller.finance.csv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.mock.web.MockMultipartFile;

class BankCsvParserTest {

    @Nested
    class ParseLine {
        @ParameterizedTest
        @CsvSource({
            "'\"A\";\"B\";\"C\"', A, B, C",
            "' \"A\" ; B ; C ', ' \"A', B, 'C '",
            "'\"A\" ; \"B\" ; 1,00', A, B, '1,00'"
        })
        void splitsSemicolonSeparatedLine(String line, String a, String b, String c) {
            assertThat(BankCsvParser.parseLine(line)).containsExactly(a, b, c);
        }
    }

    @Nested
    class StaticParse {
        @Test
        void throwsForNoValidParser() throws IOException {
            String invalid = IntStream.range(0, 15).mapToObj(i -> "x").collect(Collectors.joining("\n"));
            MockMultipartFile file = new MockMultipartFile("f", invalid.getBytes());
            assertThatThrownBy(() -> BankCsvParser.parse(file, Map.of())).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void throwsForMultipleValidParsers() throws IOException {
            String camtHeader = "\"Auftragskonto\";\"Buchungstag\";\"Valutadatum\";\"Buchungstext\";"
                    + "\"Verwendungszweck\";\"Glaeubiger ID\";\"Mandatsreferenz\";\"Kundenreferenz(End-to-End)\";"
                    + "\"Sammlerreferenz\";\"Lastschrift Ursprungsbetrag\";\"Auslagenersatz Ruecklastschrift\";"
                    + "\"Beguenstigter/Zahlungspflichtiger\";\"Kontonummer/IBAN\";\"BIC (SWIFT-Code)\";"
                    + "\"Betrag\";\"Waehrung\";\"Info\"";
            String ingHeader = "\"Buchung;Wertstellungsdatum;Auftraggeber/Empf\uFFFDnger;Buchungstext;"
                    + "Verwendungszweck;Saldo;W\uFFFDhrung;Betrag;W\uFFFDhrung\"";

            StringBuilder sb = new StringBuilder();
            sb.append(camtHeader).append('\n'); // line0
            sb.append('x').append('\n'); // line1
            sb.append("IBAN;DE12\n"); // line2
            IntStream.range(3, 13).forEach(i -> sb.append('x').append('\n')); // lines3-12
            sb.append(ingHeader).append('\n'); // line13
            sb.append('d'); // line14

            MockMultipartFile file = new MockMultipartFile("f", sb.toString().getBytes());
            assertThatThrownBy(() -> BankCsvParser.parse(file, Map.of())).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void returnsResultFromMatchingParser() throws IOException {
            String columnNames =
                    "\"Buchung;Wertstellungsdatum;Auftraggeber/Empf\uFFFDnger;Buchungstext;Verwendungszweck;Saldo;W\uFFFDhrung;Betrag;W\uFFFDhrung\"";
            String unquoted =
                    "Buchung;Wertstellungsdatum;Auftraggeber/Empf\uFFFDnger;Buchungstext;Verwendungszweck;Saldo;W\uFFFDhrung;Betrag;W\uFFFDhrung";
            StringBuilder sb = new StringBuilder();
            sb.append("Header1\nHeader2\nIBAN;DE55\n");
            IntStream.range(0, 10).forEach(i -> sb.append('x').append('\n'));
            sb.append(columnNames).append('\n'); // index13
            sb.append(unquoted).append('\n'); // index14
            sb.append("01.01.2025;01.01.2025;Counter;Text;Purpose;100,00;EUR;5,00;EUR");
            MockMultipartFile file = new MockMultipartFile("f", sb.toString().getBytes());
            Account acc = new Account();
            acc.setIban("DE55");
            List<BankTransaction> result = BankCsvParser.parse(file, Map.of("DE55", acc));
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getAccount()).isSameAs(acc);
        }
    }
}
