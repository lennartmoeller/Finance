package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.csv.CamtV8CsvParser;
import com.lennartmoeller.finance.csv.IngV1CsvParser;
import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import com.lennartmoeller.finance.mapper.BankTransactionMapper;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.BankType;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BankCsvImportService {

    private final BankTransactionRepository repository;
    private final AccountRepository accountRepository;
    private final BankTransactionMapper mapper;
    private final IngV1CsvParser ingParser;
    private final CamtV8CsvParser camtParser;

    public List<BankTransactionDTO> importCsv(BankType type, MultipartFile file) throws IOException {
        List<? extends BankTransactionDTO> parsed;
        try (var is = file.getInputStream()) {
            parsed = switch (type) {
                case ING_V1 -> ingParser.parse(is);
                case CAMT_V8 -> camtParser.parse(is);
            };
        }

        return parsed.stream()
                .sorted(Comparator.comparing(BankTransactionDTO::getBookingDate))
                .map(dto -> {
                    String iban =
                            switch (dto) {
                                case IngV1TransactionDTO ing -> ing.getIban();
                                case CamtV8TransactionDTO camt -> camt.getIban();
                                default -> null;
                            };
                    var account = accountRepository.findByIban(iban);
                    if (account.isEmpty()) {
                        return null;
                    }
                    dto.setAccountId(account.get().getId());
                    boolean exists = repository.existsByAccountAndBookingDateAndPurposeAndCounterpartyAndAmount(
                            account.get(),
                            dto.getBookingDate(),
                            dto.getPurpose(),
                            dto.getCounterparty(),
                            dto.getAmount());
                    if (exists) {
                        return null;
                    }
                    BankTransaction entity =
                            switch (dto) {
                                case IngV1TransactionDTO ing -> mapper.toEntity(ing, accountRepository);
                                case CamtV8TransactionDTO camt -> mapper.toEntity(camt, accountRepository);
                                default -> mapper.toEntity(dto, accountRepository);
                            };
                    BankTransaction persisted = repository.save(entity);
                    return mapper.toDto(persisted);
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }
}
