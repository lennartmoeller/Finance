package com.lennartmoeller.finance.csv;

import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import com.lennartmoeller.finance.model.BankType;
import com.lennartmoeller.finance.util.EuroParser;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;

@Component
public class CamtV8CsvParser implements BankCsvParser<CamtV8TransactionDTO> {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yy");

    @Override
    public List<CamtV8TransactionDTO> parse(InputStream inputStream) {
        List<String> lines = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .toList();
        if (lines.isEmpty()) {
            return List.of();
        }
        String[] headers = parseLine(lines.getFirst());
        Map<String, Integer> idx =
                IntStream.range(0, headers.length).boxed().collect(Collectors.toMap(i -> headers[i], i -> i));

        return IntStream.range(1, lines.size())
                .mapToObj(i -> buildDto(lines.get(i), headers, idx))
                .flatMap(Optional::stream)
                .toList();
    }

    private Optional<CamtV8TransactionDTO> buildDto(String line, String[] headers, Map<String, Integer> idx) {
        if (line.isBlank()) {
            return Optional.empty();
        }
        String[] values = parseLine(line);
        if (values.length < headers.length) {
            return Optional.empty();
        }
        String iban = values[idx.get("Auftragskonto")].replaceAll("\\s+", "");

        return Optional.of(CamtV8TransactionDTO.builder()
                .bank(BankType.CAMT_V8)
                .iban(iban)
                .bookingDate(LocalDate.parse(values[idx.get("Buchungstag")], DATE))
                .valueDate(LocalDate.parse(values[idx.get("Valutadatum")], DATE))
                .bookingText(values[idx.get("Buchungstext")])
                .purpose(values[idx.get("Verwendungszweck")])
                .counterparty(values[idx.get("Beguenstigter/Zahlungspflichtiger")])
                .amount(EuroParser.parseToCents(values[idx.get("Betrag")]).orElse(null))
                .currency(values[idx.get("Waehrung")])
                .creditorId(values[idx.get("Glaeubiger ID")])
                .mandateReference(values[idx.get("Mandatsreferenz")])
                .customerReference(values[idx.get("Kundenreferenz (End-to-End)")])
                .collectorReference(values[idx.get("Sammlerreferenz")])
                .directDebitOriginalAmount(values[idx.get("Lastschrift Ursprungsbetrag")])
                .refundFee(values[idx.get("Auslagenersatz Ruecklastschrift")])
                .bic(values[idx.get("BIC (SWIFT-Code)")])
                .info(values[idx.get("Info")])
                .build());
    }

    private String[] parseLine(String line) {
        line = line.trim();
        if (line.startsWith("\uFEFF")) {
            line = line.substring(1);
        }
        if (line.startsWith("\"") && line.endsWith("\"")) {
            line = line.substring(1, line.length() - 1);
        }
        return line.split("\";\"");
    }
}
