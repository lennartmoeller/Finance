package com.lennartmoeller.finance.csv;

import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.springframework.web.multipart.MultipartFile;

public abstract class BankCsvParser {
    protected final List<String> lines;

    protected BankCsvParser(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            this.lines = (new BufferedReader(inputStreamReader)).lines().toList();
        }
    }

    protected abstract boolean isValid();

    public static List<BankTransaction> parse(MultipartFile file, Map<String, Account> accountsByIban)
            throws IOException {
        List<BankCsvParser> validParsers = Stream.of(new IngV1CsvParser(file), new CamtV8CsvParser(file))
                .filter(BankCsvParser::isValid)
                .toList();

        if (validParsers.isEmpty()) {
            throw new IllegalArgumentException("No valid parser found for the provided CSV file.");
        }
        if (validParsers.size() > 1) {
            throw new IllegalArgumentException(
                    "Multiple valid parsers found for the provided CSV file. Please ensure only one format is used.");
        }

        return validParsers.getFirst().parse(accountsByIban);
    }

    public static List<String> parseLine(String line) {
        return Stream.of(line.split("\\s*;\\s*"))
                .map(s -> s.replaceAll("(^\")|(\"$)", ""))
                .toList();
    }

    public List<BankTransaction> parse(Map<String, Account> accountsByIban) {
        Map<String, String> header = extractHeader();
        int dataStartLineIndex = getDataStartLineIndex();

        return lines.stream()
                .skip(dataStartLineIndex)
                .map(line -> {
                    List<String> values = parseLine(line);
                    return buildEntity(header, line, values, accountsByIban);
                })
                .toList();
    }

    protected Map<String, String> extractHeader() {
        return Map.of();
    }

    protected int getDataStartLineIndex() {
        return 1;
    }

    protected abstract @Nullable BankTransaction buildEntity(
            Map<String, String> header, String line, List<String> values, Map<String, Account> accountsByIban);
}
