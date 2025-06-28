package com.lennartmoeller.finance.csv;

import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
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
public class IngV1CsvParser implements BankCsvParser<IngV1TransactionDTO> {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    public List<IngV1TransactionDTO> parse(InputStream inputStream) throws IOException {
        List<String> lines = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .toList();
        String iban = lines.stream()
                .filter(l -> l.startsWith("IBAN;"))
                .map(l -> l.split(";", 2)[1])
                .findFirst()
                .orElse("");
        iban = iban.replaceAll("\\s+", "");
        int headerIdx = 0;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith("Buchung;")) {
                headerIdx = i;
                break;
            }
        }
        String[] headers = lines.get(headerIdx).split(";");
        List<IngV1TransactionDTO> result = new ArrayList<>();
        for (int idx = headerIdx + 1; idx < lines.size(); idx++) {
            String line = lines.get(idx);
            if (line.isBlank()) {
                continue;
            }
            String[] values = line.split(";", -1);
            if (values.length < headers.length) {
                continue;
            }
            Map<String, String> map = new LinkedHashMap<>();
            for (int i = 0; i < headers.length; i++) {
                String key = headers[i];
                if (map.containsKey(key)) {
                    key = key + '_' + i;
                }
                map.put(key, values[i]);
            }
            map.put("IBAN", iban);

            IngV1TransactionDTO dto = new IngV1TransactionDTO();
            dto.setBank(BankType.ING_V1);
            dto.setIban(iban);
            dto.setBookingDate(LocalDate.parse(values[0], DATE));
            dto.setValueDate(LocalDate.parse(values[1], DATE));
            dto.setCounterparty(values[2]);
            dto.setBookingText(values[3]);
            dto.setPurpose(values[4]);
            dto.setBalance(EuroParser.parseToCents(values[5]));
            dto.setBalanceCurrency(values[6]);
            dto.setAmount(EuroParser.parseToCents(values[7]));
            dto.setAmountCurrency(values[8]);
            dto.setData(map);
            result.add(dto);
        }
        return result;
    }
}
