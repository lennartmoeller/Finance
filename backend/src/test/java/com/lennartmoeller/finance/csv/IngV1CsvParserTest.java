package com.lennartmoeller.finance.csv;

import static org.assertj.core.api.Assertions.assertThat;

import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import com.lennartmoeller.finance.model.BankType;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class IngV1CsvParserTest {

    @Test
    void shouldReturnEmptyWhenHeaderIsMissing() {
        String csv = "IBAN;DE12\nfoo";
        IngV1CsvParser parser = new IngV1CsvParser();

        List<IngV1TransactionDTO> result = parser.parse(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldParseTransactionAndSanitizeIban() {
        String csv = "Umsatzanzeige;Datei\n" + "IBAN;DE12 3456 7890 1234 5678 90\n"
                + "Buchung;Wertstellungsdatum;Auftraggeber;Buchungstext;Verwendungszweck;Saldo;W\u00E4hrung;Betrag;W\u00E4hrung\n"
                + "01.01.2025;01.01.2025;Counter;Text;Purpose;100,00;EUR;5,00;EUR\n";
        IngV1CsvParser parser = new IngV1CsvParser();

        List<IngV1TransactionDTO> result = parser.parse(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));

        assertThat(result).hasSize(1);
        IngV1TransactionDTO dto = result.getFirst();
        assertThat(dto.getBank()).isEqualTo(BankType.ING_V1);
        assertThat(dto.getIban()).isEqualTo("DE12345678901234567890");
        assertThat(dto.getBookingDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(dto.getBalance()).isEqualTo(10000L);
        assertThat(dto.getAmount()).isEqualTo(500L);
    }

    @Test
    void shouldHandleDuplicateHeadersAndSkipInvalidLines() {
        String csv = "IBAN;DE12\n"
                + "Buchung;Wertstellungsdatum;Auftraggeber;Buchungstext;Verwendungszweck;Saldo;W\u00E4hrung;Betrag;W\u00E4hrung;W\u00E4hrung\n"
                + "\n"
                + "01.01.2025;01.01.2025;Counter;Text;Purpose;100,00;EUR;5,00;EUR;EUR\n"
                + "01.01;01.01\n"; // invalid
        IngV1CsvParser parser = new IngV1CsvParser();

        List<IngV1TransactionDTO> result = parser.parse(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));

        assertThat(result).hasSize(1);
        IngV1TransactionDTO dto = result.getFirst();
        assertThat(dto.getData()).containsEntry("W\u00E4hrung", "EUR");
        assertThat(dto.getData()).containsEntry("W\u00E4hrung_9", "EUR");
    }
}
