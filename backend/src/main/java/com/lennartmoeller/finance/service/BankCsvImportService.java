package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.csv.BankCsvParser;
import com.lennartmoeller.finance.dto.BankCsvImportStatsDTO;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BankCsvImportService {
    private final AccountRepository accountRepository;
    private final BankTransactionRepository transactionRepository;
    private final TransactionLinkSuggestionService suggestionService;

    public BankCsvImportStatsDTO importCsv(MultipartFile file) throws IOException {
        Map<String, Account> accountsByIban = accountRepository.findByIbanIsNotNull().stream()
                .collect(Collectors.toMap(Account::getIban, Function.identity()));

        List<BankTransaction> entities = BankCsvParser.parse(file, accountsByIban);
        List<BankTransaction> entitiesNonNull =
                entities.stream().filter(Objects::nonNull).toList();
        List<BankTransaction> existings = transactionRepository.findAll();

        List<BankTransaction> toSave = entitiesNonNull.stream()
                .filter(entity -> existings.stream()
                        .noneMatch(existing -> existing.getData().equals(entity.getData())
                                || existing.getAccount()
                                                .getId()
                                                .equals(entity.getAccount().getId())
                                        && existing.getBookingDate().equals(entity.getBookingDate())
                                        && existing.getPurpose().equals(entity.getPurpose())
                                        && existing.getCounterparty().equals(entity.getCounterparty())
                                        && existing.getAmount().equals(entity.getAmount())))
                .toList();
        List<BankTransaction> saved = transactionRepository.saveAll(toSave);

        suggestionService.updateAllFor(saved, null);

        BankCsvImportStatsDTO importStats = new BankCsvImportStatsDTO();
        importStats.setImports(saved.size());
        importStats.setErrors(entities.size() - entitiesNonNull.size());
        importStats.setDuplicates(entitiesNonNull.size() - toSave.size());
        return importStats;
    }
}
