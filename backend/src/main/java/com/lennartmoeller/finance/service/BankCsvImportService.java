package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.converter.MapToJsonStringConverter;
import com.lennartmoeller.finance.csv.CamtV8CsvParser;
import com.lennartmoeller.finance.csv.IngV1CsvParser;
import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.mapper.BankTransactionMapper;
import com.lennartmoeller.finance.mapper.CamtV8TransactionMapper;
import com.lennartmoeller.finance.mapper.IngV1TransactionMapper;
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
    private final IngV1TransactionMapper ingMapper;
    private final CamtV8TransactionMapper camtMapper;
    private final BankTransactionRepository transactionRepository;
    private final CamtV8CsvParser camtParser;
    private final IngV1CsvParser ingParser;
    private final MapToJsonStringConverter converter;
    private final TransactionLinkSuggestionService suggestionService;

    public List<BankTransactionDTO> importCsv(BankType bankType, MultipartFile file) throws IOException {
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
                .sorted(Comparator.comparing(BankTransactionDTO::getBookingDate))
                .map(dto -> {
                    Account account = accountsByIban.get(dto.getIban());
                    BankTransactionMapper mapper = bankType == BankType.ING_V1 ? ingMapper : camtMapper;
                    BankTransaction entity = mapper.toEntity(dto, account);
                    Map<String, String> map = mapper.toDataMap(dto);
                    entity.getData().clear();
                    entity.getData().putAll(map);
                    return Map.entry(dto, entity);
                })
                .toList();

        List<String> datas = transactionRepository.findAllDatas().stream()
                .map(converter::convertToDatabaseColumn)
                .toList();
        var partition = entries.stream().collect(Collectors.partitioningBy(e -> shouldGetSaved(e.getValue(), datas)));

        List<BankTransaction> toSave =
                partition.get(true).stream().map(Map.Entry::getValue).toList();
        List<BankTransaction> saved = toSave.isEmpty() ? List.of() : transactionRepository.saveAll(toSave);

        suggestionService.updateAllFor(saved, null);

        List<BankTransactionDTO> savedDtos =
                saved.stream().map(e -> mapperFor(e.getBank()).toDto(e)).toList();

        return savedDtos;
    }

    private BankTransactionMapper mapperFor(BankType type) {
        return type == BankType.ING_V1 ? ingMapper : camtMapper;
    }

    private boolean shouldGetSaved(BankTransaction entity, List<String> datas) {
        if (entity.getAccount() == null) {
            return false;
        }
        String data = converter.convertToDatabaseColumn(entity.getData());
        return !datas.contains(data);
    }
}
