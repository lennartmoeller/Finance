package com.lennartmoeller.finance.csv;

import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.BankType;
import com.lennartmoeller.finance.util.EuroParser;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class IngV1CsvParser extends BankCsvParser {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    protected Map<String, String> extractHeader(List<String> lines) {
        return lines.stream()
                .map(line -> {
                    String[] parts = line.split(";");
                    if (parts.length != 2) {
                        return null;
                    }
                    return Map.entry(parts[0], parts[1]);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    protected List<String> extractColumnNames(List<String> lines) {
        return lines.stream()
                .filter(line -> line.startsWith("Buchung;"))
                .findFirst()
                .map(this::parseLine)
                .orElseThrow(() -> new IllegalArgumentException("No header line found in the CSV file"));
    }

    protected BankTransaction buildEntities(
            Map<String, String> values, String line, Map<String, String> header, Map<String, Account> accountsByIban) {
        String iban = header.get("IBAN").replaceAll("\\s+", "");
        Account account = accountsByIban.get(iban);
        if (account == null) {
            return null;
        }

        BankTransaction entity = new BankTransaction();
        entity.setBank(BankType.ING_V1);
        entity.setAccount(account);
        entity.setBookingDate(LocalDate.parse(values.get("Buchung"), DATE));
        entity.setPurpose(values.get("Verwendungszweck"));
        entity.setCounterparty(values.get("Auftraggeber/Empfänger"));
        entity.setAmount(EuroParser.parseToCents(values.get("Betrag"))
                .orElseThrow(() -> new IllegalArgumentException("Invalid amount format: " + values.get("Betrag"))));
        entity.setData(line);
        return entity;
    }

    @Override
    protected List<String> parseLine(String line) {
        return List.of(line.split(";", -1));
    }
}
