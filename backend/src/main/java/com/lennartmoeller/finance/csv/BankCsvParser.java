package com.lennartmoeller.finance.csv;

import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.BankType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.web.multipart.MultipartFile;

public abstract class BankCsvParser {
    public static List<BankTransaction> parse(
            BankType bankType, MultipartFile file, Map<String, Account> accountsByIban) throws IOException {
        return switch (bankType) {
            case CAMT_V8 -> (new CamtV8CsvParser()).parse(file, accountsByIban);
            case ING_V1 -> (new IngV1CsvParser()).parse(file, accountsByIban);
        };
    }

    public List<BankTransaction> parse(MultipartFile file, Map<String, Account> accountsByIban) throws IOException {
        List<String> lines = extractLines(file);
        Map<String, String> header = extractHeader(lines);
        List<String> columnNames = extractColumnNames(lines);

        return lines.stream()
                .map(line -> {
                    Map<String, String> values = mapLine(columnNames, line);
                    return buildEntities(values, line, header, accountsByIban);
                })
                .toList();
    }

    protected abstract List<String> parseLine(String line);

    protected abstract BankTransaction buildEntities(
            Map<String, String> values, String line, Map<String, String> header, Map<String, Account> accountsByIban);

    @SuppressWarnings("unused")
    protected Map<String, String> extractHeader(List<String> lines) {
        return Map.of();
    }

    protected List<String> extractColumnNames(List<String> lines) {
        return parseLine(lines.getFirst());
    }

    protected final List<String> extractLines(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            return new BufferedReader(inputStreamReader).lines().toList();
        }
    }

    protected Map<String, String> mapLine(List<String> header, String line) {
        List<String> values = parseLine(line);
        if (values.size() != header.size()) {
            throw new IllegalArgumentException(
                    "Header and line size mismatch: " + header.size() + " vs " + values.size());
        }
        return IntStream.range(0, header.size()).boxed().collect(Collectors.toMap(header::get, values::get));
    }
}
