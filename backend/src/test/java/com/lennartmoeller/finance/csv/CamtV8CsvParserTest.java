package com.lennartmoeller.finance.csv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.BankType;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class CamtV8CsvParserTest {
    @Test
    void parsesValidFile() throws IOException {
        String csv =
                "\"Auftragskonto\";\"Buchungstag\";\"Valutadatum\";\"Buchungstext\";\"Verwendungszweck\";\"Glaeubiger ID\";\"Mandatsreferenz\";\"Kundenreferenz (End-to-End)\";\"Sammlerreferenz\";\"Lastschrift Ursprungsbetrag\";\"Auslagenersatz Ruecklastschrift\";\"Beguenstigter/Zahlungspflichtiger\";\"Kontonummer/IBAN\";\"BIC (SWIFT-Code)\";\"Betrag\";\"Waehrung\";\"Info\"\n"
                        + "DE12;01.01.24;01.01.24;Text;Purpose;CID;MID;CR;Collector;O;F;Counter;DE55;BIC;1,00;EUR;Info";
        MockMultipartFile file = new MockMultipartFile("f", csv.getBytes());
        CamtV8CsvParser parser = new CamtV8CsvParser(file);
        Account account = new Account();
        account.setIban("DE12");
        Map<String, Account> map = Map.of("DE12", account);

        List<BankTransaction> result = parser.parse(map);

        assertThat(result).hasSize(1);
        BankTransaction bt = result.getFirst();
        assertThat(bt.getBank()).isEqualTo(BankType.CAMT_V8);
        assertThat(bt.getAccount()).isSameAs(account);
        assertThat(bt.getPurpose()).isEqualTo("Purpose");
        assertThat(bt.getData()).contains("Info");
    }

    @Test
    void returnsNullEntityWhenAccountMissing() throws IOException {
        String csv =
                "\"Auftragskonto\";\"Buchungstag\";\"Valutadatum\";\"Buchungstext\";\"Verwendungszweck\";\"Glaeubiger ID\";\"Mandatsreferenz\";\"Kundenreferenz (End-to-End)\";\"Sammlerreferenz\";\"Lastschrift Ursprungsbetrag\";\"Auslagenersatz Ruecklastschrift\";\"Beguenstigter/Zahlungspflichtiger\";\"Kontonummer/IBAN\";\"BIC (SWIFT-Code)\";\"Betrag\";\"Waehrung\";\"Info\"\n"
                        + "DE12;01.01.24;01.01.24;Text;Purpose;CID;MID;CR;Collector;O;F;Counter;DE55;BIC;1,00;EUR;Info";
        MockMultipartFile file = new MockMultipartFile("f", csv.getBytes());
        CamtV8CsvParser parser = new CamtV8CsvParser(file);

        List<BankTransaction> result = parser.parse(Map.of());

        assertThat(result).containsExactly((BankTransaction) null);
    }

    @Test
    void throwsForInvalidAmount() throws IOException {
        String csv =
                "\"Auftragskonto\";\"Buchungstag\";\"Valutadatum\";\"Buchungstext\";\"Verwendungszweck\";\"Glaeubiger ID\";\"Mandatsreferenz\";\"Kundenreferenz (End-to-End)\";\"Sammlerreferenz\";\"Lastschrift Ursprungsbetrag\";\"Auslagenersatz Ruecklastschrift\";\"Beguenstigter/Zahlungspflichtiger\";\"Kontonummer/IBAN\";\"BIC (SWIFT-Code)\";\"Betrag\";\"Waehrung\";\"Info\"\n"
                        + "DE12;01.01.24;01.01.24;Text;Purpose;CID;MID;CR;Collector;O;F;Counter;DE55;BIC;abc;EUR;Info";
        MockMultipartFile file = new MockMultipartFile("f", csv.getBytes());
        CamtV8CsvParser parser = new CamtV8CsvParser(file);
        Account acc = new Account();
        acc.setIban("DE12");

        assertThatThrownBy(() -> parser.parse(Map.of("DE12", acc)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid amount");
    }

    @Nested
    class Validation {
        @Test
        void validHeaderIsRecognized() throws IOException {
            String header =
                    "\"Auftragskonto\";\"Buchungstag\";\"Valutadatum\";\"Buchungstext\";\"Verwendungszweck\";\"Glaeubiger ID\";\"Mandatsreferenz\";\"Kundenreferenz (End-to-End)\";\"Sammlerreferenz\";\"Lastschrift Ursprungsbetrag\";\"Auslagenersatz Ruecklastschrift\";\"Beguenstigter/Zahlungspflichtiger\";\"Kontonummer/IBAN\";\"BIC (SWIFT-Code)\";\"Betrag\";\"Waehrung\";\"Info\"";
            MockMultipartFile file = new MockMultipartFile("f", (header + "\n").getBytes());
            CamtV8CsvParser parser = new CamtV8CsvParser(file);

            assertThat(parser.isValid()).isTrue();
        }

        @Test
        void invalidHeaderIsRejected() throws IOException {
            String header = "WRONG";
            MockMultipartFile file = new MockMultipartFile("f", (header + "\n").getBytes());
            CamtV8CsvParser parser = new CamtV8CsvParser(file);

            assertThat(parser.isValid()).isFalse();
        }
    }
}
