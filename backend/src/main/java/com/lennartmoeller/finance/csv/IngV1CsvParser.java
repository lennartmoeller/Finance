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
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import org.springframework.web.multipart.MultipartFile;

public class IngV1CsvParser extends BankCsvParser {
    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    protected IngV1CsvParser(MultipartFile file) throws IOException {
        super(file);
    }

    @Override
    protected boolean isValid() {
        String columnNamesLine =
                "\"Buchung;Wertstellungsdatum;Auftraggeber/Empf�nger;Buchungstext;Verwendungszweck;Saldo;W�hrung;Betrag;W�hrung\"";
        boolean validIbanLine = this.lines.get(2).startsWith("IBAN;");
        boolean validColumnNameLine = this.lines.get(13).equals(columnNamesLine);
        return validColumnNameLine && validIbanLine;
    }

    @Override
    protected Map<String, String> extractHeader() {
        String[] ibanLine = this.lines.get(2).split(";");
        return Map.of(ibanLine[0], ibanLine[1]);
    }

    @Override
    protected int getDataStartLineIndex() {
        return IntStream.range(0, lines.size())
                .filter(i -> lines.get(i).startsWith("Buchung;"))
                .map(i -> i + 1)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No header line found in the CSV file"));
    }

    @Override
    protected @Nullable BankTransaction buildEntity(
            Map<String, String> header, String line, List<String> values, Map<String, Account> accountsByIban) {
        String iban = header.get("IBAN").replaceAll("\\s+", "");
        Account account = accountsByIban.get(iban);
        if (account == null) {
            return null;
        }

        BankTransaction entity = new BankTransaction();
        entity.setBank(BankType.ING_V1);
        entity.setAccount(account);
        entity.setBookingDate(LocalDate.parse(values.get(0), DATE));
        entity.setPurpose(values.get(4));
        entity.setCounterparty(values.get(2));
        String amountStr = values.get(7);
        entity.setAmount(EuroParser.parseToCents(amountStr)
                .orElseThrow(() -> new IllegalArgumentException("Invalid amount format: " + amountStr)));
        entity.setData(line);
        return entity;
    }
}
