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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import org.springframework.stereotype.Component;

@Component
public class IngV1CsvParser implements BankCsvParser<IngV1TransactionDTO> {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    @Nonnull
    public List<IngV1TransactionDTO> parse(@Nonnull InputStream inputStream) throws IOException {
        List<String> lines = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .toList();

        String iban = lines.stream()
                .filter(l -> l.startsWith("IBAN;"))
                .map(l -> l.split(";", 2)[1])
                .findFirst()
                .orElse("")
                .replaceAll("\\s+", "");

        int headerIdx = IntStream.range(0, lines.size())
                .filter(i -> lines.get(i).startsWith("Buchung;"))
                .findFirst()
                .orElse(-1);
        if (headerIdx < 0) {
            return List.of();
        }

        String[] headers = lines.get(headerIdx).split(";");

        return lines.stream()
                .skip(headerIdx + 1L)
                .map(line -> parseLine(line, headers, iban))
                .flatMap(Optional::stream)
                .toList();
    }

    private static boolean headerMapContains(String[] headers, int index) {
        String key = headers[index];
        for (int i = 0; i < index; i++) {
            if (headers[i].equals(key)) {
                return true;
            }
        }
        return false;
    }

    private Optional<IngV1TransactionDTO> parseLine(String line, String[] headers, String iban) {
        if (line.isBlank()) {
            return Optional.empty();
        }

        String[] values = line.split(";", -1);
        if (values.length < headers.length) {
            return Optional.empty();
        }

        Map<String, String> data = IntStream.range(0, headers.length)
                .boxed()
                .collect(Collectors.toMap(
                        i -> {
                            String key = headers[i];
                            return headerMapContains(headers, i) ? key + '_' + i : key;
                        },
                        i -> values[i],
                        (a, b) -> b,
                        LinkedHashMap::new));
        data.put("IBAN", iban);

        return Optional.of(IngV1TransactionDTO.builder()
                .bank(BankType.ING_V1)
                .iban(iban)
                .bookingDate(LocalDate.parse(values[0], DATE))
                .valueDate(LocalDate.parse(values[1], DATE))
                .counterparty(values[2])
                .bookingText(values[3])
                .purpose(values[4])
                .balance(EuroParser.parseToCents(values[5]).orElse(null))
                .balanceCurrency(values[6])
                .amount(EuroParser.parseToCents(values[7]).orElse(null))
                .amountCurrency(values[8])
                .data(data)
                .build());
    }
}
