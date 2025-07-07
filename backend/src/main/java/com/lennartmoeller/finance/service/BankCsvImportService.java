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
import java.util.ArrayList;
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
    private final BankTransactionRepository repository;
    private final BankTransactionMapper mapper;
    private final IngV1CsvParser ingParser;
    private final CamtV8CsvParser camtParser;
    private final AccountRepository accountRepository;
    private final TransactionLinkSuggestionService suggestionService;

    public BankTransactionImportResultDTO importCsv(BankType type, MultipartFile file) throws IOException {
        List<? extends BankTransactionDTO> parsed;
        try (InputStream is = file.getInputStream()) {
            parsed = switch (type) {
                case ING_V1 -> ingParser.parse(is);
                case CAMT_V8 -> camtParser.parse(is);
            };
        }

        Set<String> ibans = parsed.stream()
                .map(BankTransactionDTO::getIban)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, Account> accountMap = accountRepository.findAllByIbanIn(ibans).stream()
                .collect(Collectors.toMap(Account::getIban, Function.identity()));

        List<? extends Map.Entry<? extends BankTransactionDTO, BankTransaction>> mapped = parsed.stream()
                .map(dto -> {
                    Account account = accountMap.get(dto.getIban());
                    BankTransaction entity =
                            switch (dto) {
                                case IngV1TransactionDTO ing -> mapper.toEntity(ing, account);
                                case CamtV8TransactionDTO camt -> mapper.toEntity(camt, account);
                                default -> mapper.toEntity(dto, account);
                            };
                    return Map.entry(dto, entity);
                })
                .sorted(Comparator.comparing(entry -> entry.getKey().getBookingDate()))
                .toList();

        List<BankTransaction> entities =
                mapped.stream().map(Map.Entry::getValue).toList();
        List<Map<String, String>> data =
                entities.stream().map(BankTransaction::getData).toList();
        Set<Map<String, String>> existingData = repository.findAllByDataIn(data).stream()
                .map(BankTransaction::getData)
                .collect(Collectors.toSet());

        List<BankTransaction> toSave = new ArrayList<>();
        List<BankTransactionDTO> unsavedDtos = new ArrayList<>();

        for (int i = 0; i < mapped.size(); i++) {
            BankTransaction entity = entities.get(i);
            BankTransactionDTO dto = mapped.get(i).getKey();
            if (entity.getAccount() == null || existingData.contains(entity.getData())) {
                unsavedDtos.add(dto);
            } else {
                toSave.add(entity);
            }
        }

        List<BankTransaction> savedEntities = toSave.isEmpty() ? List.of() : repository.saveAll(toSave);

        suggestionService.updateForBankTransactions(savedEntities);

        List<BankTransactionDTO> savedDtos =
                savedEntities.stream().map(mapper::toDto).toList();
        return new BankTransactionImportResultDTO(savedDtos, unsavedDtos);
    }
}
