package com.lennartmoeller.finance.csv;

import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import com.lennartmoeller.finance.model.BankType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CamtV8CsvParser implements BankCsvParser<CamtV8TransactionDTO> {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yy");

    @Override
    public List<CamtV8TransactionDTO> parse(InputStream inputStream) throws IOException {
        List<String> lines = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .toList();
        if (lines.isEmpty()) {
            return List.of();
        }
        String[] headers = parseLine(lines.get(0));
        List<CamtV8TransactionDTO> result = new ArrayList<>();
        for (int r = 1; r < lines.size(); r++) {
            String line = lines.get(r);
            if (line.isBlank()) {
                continue;
            }
            String[] values = parseLine(line);
            if (values.length < headers.length) {
                continue;
            }
            Map<String, String> map = new LinkedHashMap<>();
            for (int i = 0; i < headers.length; i++) {
                map.put(headers[i], values[i]);
            }
            Map<String, Integer> idx = new java.util.HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                idx.put(headers[i], i);
            }
            CamtV8TransactionDTO dto = new CamtV8TransactionDTO();
            dto.setBank(BankType.CAMT_V8);
            dto.setIban(values[idx.get("Kontonummer/IBAN")]);
            dto.setBookingDate(LocalDate.parse(values[idx.get("Buchungstag")], DATE));
            dto.setValueDate(LocalDate.parse(values[idx.get("Valutadatum")], DATE));
            dto.setBookingText(values[idx.get("Buchungstext")]);
            dto.setPurpose(values[idx.get("Verwendungszweck")]);
            dto.setCounterparty(values[idx.get("Beguenstigter/Zahlungspflichtiger")]);
            dto.setAmount(EuroParser.parseToCents(values[idx.get("Betrag")]));
            dto.setCurrency(values[idx.get("Waehrung")]);
            dto.setCreditorId(values[idx.get("Glaeubiger ID")]);
            dto.setMandateReference(values[idx.get("Mandatsreferenz")]);
            dto.setCustomerReference(values[idx.get("Kundenreferenz (End-to-End)")]);
            dto.setCollectorReference(values[idx.get("Sammlerreferenz")]);
            dto.setDirectDebitOriginalAmount(values[idx.get("Lastschrift Ursprungsbetrag")]);
            dto.setRefundFee(values[idx.get("Auslagenersatz Ruecklastschrift")]);
            dto.setBic(values[idx.get("BIC (SWIFT-Code)")]);
            dto.setInfo(values[idx.get("Info")]);
            dto.setData(map);
            result.add(dto);
        }
        return result;
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
