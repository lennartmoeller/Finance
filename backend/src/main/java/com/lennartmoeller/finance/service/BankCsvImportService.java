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
    private final BankTransactionMapper mapper;
    private final IngV1CsvParser ingParser;
    private final CamtV8CsvParser camtParser;
    private final AccountRepository accountRepository;

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
                .map(dto -> accountRepository
                        .findByIban(dto.getIban())
                        .filter(account -> !repository.existsByAccountAndBookingDateAndPurposeAndCounterpartyAndAmount(
                                account,
                                dto.getBookingDate(),
                                dto.getPurpose(),
                                dto.getCounterparty(),
                                dto.getAmount()))
                        .map(account -> {
                            BankTransaction entity =
                                    switch (dto) {
                                        case IngV1TransactionDTO ing -> mapper.toEntity(ing);
                                        case CamtV8TransactionDTO camt -> mapper.toEntity(camt);
                                        default -> mapper.toEntity(dto);
                                    };
                            entity.setAccount(account);
                            BankTransaction persisted = repository.save(entity);
                            return mapper.toDto(persisted);
                        })
                        .orElse(null))
                .filter(java.util.Objects::nonNull)
                .toList();
    }
}
