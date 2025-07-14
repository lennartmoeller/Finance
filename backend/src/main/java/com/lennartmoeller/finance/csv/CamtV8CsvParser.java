package com.lennartmoeller.finance.csv;

import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.BankType;
import com.lennartmoeller.finance.util.EuroParser;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CamtV8CsvParser extends BankCsvParser {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yy");

    @Override
    protected BankTransaction buildEntities(
            Map<String, String> values, String line, Map<String, String> header, Map<String, Account> accountsByIban) {
        String iban = values.get("Auftragskonto").replaceAll("\\s+", "");
        Account account = accountsByIban.get(iban);
        if (account == null) {
            return null;
        }

        BankTransaction entity = new BankTransaction();
        entity.setBank(BankType.CAMT_V8);
        entity.setAccount(account);
        entity.setBookingDate(LocalDate.parse(values.get("Buchungstag"), DATE));
        entity.setPurpose(values.get("Verwendungszweck"));
        entity.setCounterparty(values.get("Beguenstigter/Zahlungspflichtiger"));
        entity.setAmount(EuroParser.parseToCents(values.get("Betrag"))
                .orElseThrow(() -> new IllegalArgumentException("Invalid amount format: " + values.get("Betrag"))));
        entity.setData(line);
        return entity;
    }

    @Override
    protected List<String> parseLine(String line) {
        line = line.trim();
        if (line.startsWith("\uFEFF")) {
            line = line.substring(1);
        }
        if (line.startsWith("\"") && line.endsWith("\"")) {
            line = line.substring(1, line.length() - 1);
        }
        return List.of(line.split("\";\""));
    }
}
