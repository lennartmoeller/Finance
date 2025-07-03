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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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

    public BankTransactionImportResultDTO importCsv(BankType type, MultipartFile file) throws IOException {
        List<? extends BankTransactionDTO> parsed;
        try (var is = file.getInputStream()) {
            parsed = switch (type) {
                case ING_V1 -> ingParser.parse(is);
                case CAMT_V8 -> camtParser.parse(is);
            };
        }

        Set<String> ibans = parsed.stream()
                .map(BankTransactionDTO::getIban)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, Account> accountMap =
                accountRepository.findAllByIbanIn(ibans).stream().collect(Collectors.toMap(Account::getIban, a -> a));

        List<BankTransactionDTO> saved = new java.util.ArrayList<>();
        List<BankTransactionDTO> unsaved = new java.util.ArrayList<>();

        parsed.stream()
                .sorted(Comparator.comparing(BankTransactionDTO::getBookingDate))
                .forEach(dto -> processDto(dto, accountMap, saved, unsaved));

        return new BankTransactionImportResultDTO(saved, unsaved);
    }

    private void processDto(
            BankTransactionDTO dto,
            Map<String, Account> accountMap,
            List<BankTransactionDTO> saved,
            List<BankTransactionDTO> unsaved) {
        Account account = accountMap.get(dto.getIban());
        BankTransaction entity =
                switch (dto) {
                    case IngV1TransactionDTO ing -> mapper.toEntity(ing, account);
                    case CamtV8TransactionDTO camt -> mapper.toEntity(camt, account);
                    default -> mapper.toEntity(dto, account);
                };

        if (entity.getAccount() == null) {
            unsaved.add(dto);
            return;
        }

        boolean exists = repository.existsByAccountAndBookingDateAndPurposeAndCounterpartyAndAmount(
                entity.getAccount(),
                entity.getBookingDate(),
                entity.getPurpose(),
                entity.getCounterparty(),
                entity.getAmount());
        if (exists) {
            unsaved.add(dto);
            return;
        }

        BankTransaction persisted = repository.save(entity);
        saved.add(mapper.toDto(persisted));
    }
}
