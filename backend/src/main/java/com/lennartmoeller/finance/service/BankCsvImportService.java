package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.csv.CamtV8CsvParser;
import com.lennartmoeller.finance.csv.IngV1CsvParser;
import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.BankTransactionImportResultDTO;
import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import com.lennartmoeller.finance.mapper.BankTransactionMapper;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.BankType;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BankCsvImportService {
    private final AccountRepository accountRepository;
    private final BankTransactionMapper mapper;
    private final BankTransactionRepository transactionRepository;
    private final CamtV8CsvParser camtParser;
    private final IngV1CsvParser ingParser;
    private final TransactionLinkSuggestionService suggestionService;

    public BankTransactionImportResultDTO importCsv(BankType bankType, MultipartFile file) throws IOException {
        List<? extends BankTransactionDTO> dtos;
        try (InputStream inputStream = file.getInputStream()) {
            dtos = switch (bankType) {
                case ING_V1 -> ingParser.parse(inputStream);
                case CAMT_V8 -> camtParser.parse(inputStream);
            };
        }

        Set<String> ibans = dtos.stream()
                .map(BankTransactionDTO::getIban)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, Account> accountsByIban = accountRepository.findAllByIbanIn(ibans).stream()
                .collect(Collectors.toMap(Account::getIban, Function.identity()));

        List<? extends Map.Entry<? extends BankTransactionDTO, BankTransaction>> entries = dtos.stream()
                .map(dto -> {
                    Account account = accountsByIban.get(dto.getIban());
                    BankTransaction entity =
                            switch (dto) {
                                case IngV1TransactionDTO ing -> mapper.toEntity(ing, account);
                                case CamtV8TransactionDTO camt -> mapper.toEntity(camt, account);
                                default ->
                                    throw new IllegalStateException("Unsupported BankTransactionDTO type: " + dto);
                            };
                    return Map.entry(dto, entity);
                })
                .sorted(Comparator.comparing(e -> e.getKey().getBookingDate()))
                .toList();

        Set<Map<String, String>> allIncomingData =
                entries.stream().map(e -> e.getValue().getData()).collect(Collectors.toSet());

        Set<Map<String, String>> existingData = transactionRepository.findAllByDataIn(allIncomingData).stream()
                .map(BankTransaction::getData)
                .collect(Collectors.toSet());

        var partition = entries.stream()
                .collect(Collectors.partitioningBy(e -> e.getValue().getAccount() != null
                        && !existingData.contains(e.getValue().getData())));

        List<BankTransaction> toSave =
                partition.get(true).stream().map(Map.Entry::getValue).toList();
        List<BankTransaction> saved = toSave.isEmpty() ? List.of() : transactionRepository.saveAll(toSave);

        suggestionService.updateForBankTransactions(saved);

        List<BankTransactionDTO> savedDtos = saved.stream().map(mapper::toDto).toList();
        List<BankTransactionDTO> skippedDtos =
                partition.get(false).stream().map(Map.Entry::getKey).collect(Collectors.toList());

        return new BankTransactionImportResultDTO(savedDtos, skippedDtos);
    }
}
