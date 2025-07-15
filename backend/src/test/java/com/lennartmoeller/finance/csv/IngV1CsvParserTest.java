package com.lennartmoeller.finance.csv;

import static org.assertj.core.api.Assertions.assertThat;

import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.BankType;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class IngV1CsvParserTest {
    @Test
    void parsesValidFile() throws IOException {
        String csv = "Header1\nHeader2\nIBAN;DE12\n"
                + "Buchung;Wertstellungsdatum;Auftraggeber/Empf\uFFFDnger;Buchungstext;Verwendungszweck;Saldo;W\uFFFDhrung;Betrag;W\uFFFDhrung\n"
                + "01.01.2025;01.01.2025;Counter;Text;Purpose;100,00;EUR;5,00;EUR";
        MockMultipartFile file = new MockMultipartFile("f", csv.getBytes());
        IngV1CsvParser parser = new IngV1CsvParser(file);
        Account acc = new Account();
        acc.setIban("DE12");
        Map<String, Account> map = Map.of("DE12", acc);

        List<BankTransaction> result = parser.parse(map);

        assertThat(result).hasSize(1);
        BankTransaction bt = result.getFirst();
        assertThat(bt.getBank()).isEqualTo(BankType.ING_V1);
        assertThat(bt.getAccount()).isSameAs(acc);
        assertThat(bt.getPurpose()).isEqualTo("Purpose");
        assertThat(bt.getData()).contains("Counter");
    }

    @Test
    void returnsNullEntityWhenAccountMissing() throws IOException {
        String csv = "Header1\nHeader2\nIBAN;DE12\n"
                + "Buchung;Wertstellungsdatum;Auftraggeber/Empf\uFFFDnger;Buchungstext;Verwendungszweck;Saldo;W\uFFFDhrung;Betrag;W\uFFFDhrung\n"
                + "01.01.2025;01.01.2025;Counter;Text;Purpose;100,00;EUR;5,00;EUR";
        MockMultipartFile file = new MockMultipartFile("f", csv.getBytes());
        IngV1CsvParser parser = new IngV1CsvParser(file);

        List<BankTransaction> result = parser.parse(Map.of());

        assertThat(result).containsExactly((BankTransaction) null);
    }
}
