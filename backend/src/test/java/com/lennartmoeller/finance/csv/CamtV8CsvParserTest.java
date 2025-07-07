package com.lennartmoeller.finance.csv;

import static org.assertj.core.api.Assertions.assertThat;

import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import com.lennartmoeller.finance.model.BankType;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class CamtV8CsvParserTest {

    @Test
    void shouldReturnEmptyListWhenInputIsEmpty() {
        CamtV8CsvParser parser = new CamtV8CsvParser();

        List<CamtV8TransactionDTO> result = parser.parse(new ByteArrayInputStream(new byte[0]));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldParseValidCsvAndSkipInvalidLines() {
        String csv =
                "\uFEFF\"Auftragskonto\";\"Buchungstag\";\"Valutadatum\";\"Buchungstext\";\"Verwendungszweck\";\"Glaeubiger ID\";\"Mandatsreferenz\";\"Kundenreferenz (End-to-End)\";\"Sammlerreferenz\";\"Lastschrift Ursprungsbetrag\";\"Auslagenersatz Ruecklastschrift\";\"Beguenstigter/Zahlungspflichtiger\";\"Kontonummer/IBAN\";\"BIC (SWIFT-Code)\";\"Betrag\";\"Waehrung\";\"Info\"\n"
                        + "\"DE12 3456\";\"01.01.24\";\"01.01.24\";\"Booking\";\"Purpose\";\"CID\";\"MID\";\"CR\";\"Collector\";\"Original\";\"Fee\";\"Counter\";\"DE55 6666\";\"BIC\";\"1,00\";\"EUR\";\"Info\"\n"
                        + "\n"
                        + "DE12;01.01.24\n"; // invalid line

        CamtV8CsvParser parser = new CamtV8CsvParser();
        List<CamtV8TransactionDTO> result =
                parser.parse(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));

        assertThat(result).hasSize(1);
        CamtV8TransactionDTO dto = result.getFirst();
        assertThat(dto.getBank()).isEqualTo(BankType.CAMT_V8);
        assertThat(dto.getIban()).isEqualTo("DE123456");
        assertThat(dto.getBookingDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(dto.getAmount()).isEqualTo(100L);
        assertThat(dto.getData()).containsEntry("BIC (SWIFT-Code)", "BIC");
    }

    @Test
    void parseLineHandlesBomAndQuotes() throws Exception {
        CamtV8CsvParser parser = new CamtV8CsvParser();
        Method method = CamtV8CsvParser.class.getDeclaredMethod("parseLine", String.class);
        method.setAccessible(true);

        String[] tokens = (String[]) method.invoke(parser, "\uFEFF\"A\";\"B\"");
        assertThat(tokens).containsExactly("A", "B");

        String[] plain = (String[]) method.invoke(parser, "A;B");
        assertThat(plain).containsExactly("A;B");
    }
}
