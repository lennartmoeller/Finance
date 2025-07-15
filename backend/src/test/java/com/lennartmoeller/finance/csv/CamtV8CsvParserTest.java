package com.lennartmoeller.finance.csv;

import static org.assertj.core.api.Assertions.assertThat;

import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.BankType;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class CamtV8CsvParserTest {

    @Test
    void shouldReturnEmptyListWhenInputIsEmpty() throws Exception {
        MockMultipartFile file = new MockMultipartFile("f", "f.csv", "text/csv", new byte[0]);
        CamtV8CsvParser parser = new CamtV8CsvParser(file);

        List<BankTransaction> result = parser.parse(Map.of());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldParseValidCsvAndSkipInvalidLines() throws Exception {
        String csv =
                """
                        \uFEFF"Auftragskonto";"Buchungstag";"Valutadatum";"Buchungstext";"Verwendungszweck";"Glaeubiger ID";"Mandatsreferenz";"Kundenreferenz (End-to-End)";"Sammlerreferenz";"Lastschrift Ursprungsbetrag";"Auslagenersatz Ruecklastschrift";"Beguenstigter/Zahlungspflichtiger";"Kontonummer/IBAN";"BIC (SWIFT-Code)";"Betrag";"Waehrung";"Info"
                        "DE12 3456";"01.01.24";"01.01.24";"Booking";"Purpose";"CID";"MID";"CR";"Collector";"Original";"Fee";"Counter";"DE55 6666";"BIC";"1,00";"EUR";"Info"

                        DE12;01.01.24
                        """; // invalid line

        MockMultipartFile file = new MockMultipartFile("f", "f.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));
        CamtV8CsvParser parser = new CamtV8CsvParser(file);
        Account account = new Account();
        account.setIban("DE123456");

        List<BankTransaction> result = parser.parse(Map.of("DE123456", account));

        assertThat(result).hasSize(3);
        BankTransaction bt = result.getFirst();
        assertThat(bt.getBank()).isEqualTo(BankType.CAMT_V8);
        assertThat(bt.getAccount()).isSameAs(account);
        assertThat(bt.getBookingDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(bt.getAmount()).isEqualTo(100L);
        assertThat(bt.getData()).contains("BIC");
        assertThat(result.stream().filter(b -> b != null).count()).isEqualTo(1);
    }

    @Test
    void parseLineHandlesBomAndQuotes() {
        List<String> tokens = BankCsvParser.parseLine("\uFEFF\"A\";\"B\"");
        assertThat(tokens.getFirst()).startsWith("\uFEFF\"").endsWith("A");
        assertThat(tokens.getLast()).isEqualTo("B");

        List<String> plain = BankCsvParser.parseLine("A;B");
        assertThat(plain).containsExactly("A", "B");
    }

    @Test
    void parseLineRemovesBomWithoutQuotes() {
        List<String> tokens = BankCsvParser.parseLine("\uFEFFA;B");
        assertThat(tokens.getFirst()).startsWith("\uFEFFA");
        assertThat(tokens.getLast()).isEqualTo("B");
    }

    @Test
    void shouldIgnoreBlankAndShortLines() throws Exception {
        String csv =
                """
                        "Auftragskonto";"Buchungstag";"Valutadatum";"Buchungstext";"Verwendungszweck";"Glaeubiger ID";"Mandatsreferenz";"Kundenreferenz (End-to-End)";"Sammlerreferenz";"Lastschrift Ursprungsbetrag";"Auslagenersatz Ruecklastschrift";"Beguenstigter/Zahlungspflichtiger";"Kontonummer/IBAN";"BIC (SWIFT-Code)";"Betrag";"Waehrung";"Info"

                        "DE12";"01.01.24";"01.01.24";"Booking";"Purpose";"CID";"MID";"CR";"Collector";"Original";"Fee";"Counter";"DE55";"BIC";"1,00";"EUR";"Info"
                        short""";

        MockMultipartFile file = new MockMultipartFile("f", "f.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));
        CamtV8CsvParser parser = new CamtV8CsvParser(file);
        Account account = new Account();
        account.setIban("DE12");

        List<BankTransaction> result = parser.parse(Map.of("DE12", account));

        assertThat(result).hasSize(3);
        BankTransaction bt = result.stream().filter(b -> b != null).findFirst().orElse(null);
        assertThat(bt).isNotNull();
        assertThat(bt.getAccount()).isSameAs(account);
        assertThat(bt.getData()).contains("BIC");
    }
}
