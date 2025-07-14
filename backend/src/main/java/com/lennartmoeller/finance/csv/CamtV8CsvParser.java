package com.lennartmoeller.finance.csv;

import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.BankType;
import com.lennartmoeller.finance.util.EuroParser;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.springframework.web.multipart.MultipartFile;

public class CamtV8CsvParser extends BankCsvParser {
    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yy");

    protected CamtV8CsvParser(MultipartFile file) throws IOException {
        super(file);
    }

    @Override
    protected boolean isValid() {
        return this.lines
                .getFirst()
                .equals(
                        "\"Auftragskonto\";\"Buchungstag\";\"Valutadatum\";\"Buchungstext\";\"Verwendungszweck\";\"Glaeubiger ID\";\"Mandatsreferenz\";\"Kundenreferenz (End-to-End)\";\"Sammlerreferenz\";\"Lastschrift Ursprungsbetrag\";\"Auslagenersatz Ruecklastschrift\";\"Beguenstigter/Zahlungspflichtiger\";\"Kontonummer/IBAN\";\"BIC (SWIFT-Code)\";\"Betrag\";\"Waehrung\";\"Info\"");
    }

    @Override
    protected @Nullable BankTransaction buildEntity(
            Map<String, String> header, String line, List<String> values, Map<String, Account> accountsByIban) {
        String iban = values.getFirst().replaceAll("\\s+", "");
        Account account = accountsByIban.get(iban);
        if (account == null) {
            return null;
        }

        BankTransaction entity = new BankTransaction();
        entity.setBank(BankType.CAMT_V8);
        entity.setAccount(account);
        entity.setBookingDate(LocalDate.parse(values.get(1), DATE));
        entity.setPurpose(values.get(4));
        entity.setCounterparty(values.get(11));
        String amountStr = values.get(14);
        entity.setAmount(EuroParser.parseToCents(amountStr)
                .orElseThrow(() -> new IllegalArgumentException("Invalid amount format: " + amountStr)));
        entity.setData(line);
        return entity;
    }
}
