package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.converter.MapToJsonStringConverter;
import com.lennartmoeller.finance.csv.CamtV8CsvParser;
import com.lennartmoeller.finance.csv.IngV1CsvParser;
import com.lennartmoeller.finance.dto.BankTransactionDTO;
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
import java.util.LinkedHashMap;
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
                    BankTransaction entity = mapper.toEntity(dto, account);
                    Map<String, String> map = buildDataMap(dto);
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

        List<BankTransactionDTO> savedDtos = saved.stream().map(mapper::toDto).toList();

        return savedDtos;
    }

    private boolean shouldGetSaved(BankTransaction entity, List<String> datas) {
        if (entity.getAccount() == null) {
            return false;
        }
        String data = converter.convertToDatabaseColumn(entity.getData());
        return !datas.contains(data);
    }

    private Map<String, String> buildDataMap(BankTransactionDTO dto) {
        Map<String, String> map = new LinkedHashMap<>();
        BankType type = dto.getBank();
        if (type == null) {
            type = dto instanceof IngV1TransactionDTO ? BankType.ING_V1 : BankType.CAMT_V8;
        }
        map.put("bank", type.name());
        map.put("iban", dto.getIban());
        map.put(
                "bookingDate",
                dto.getBookingDate() == null ? null : dto.getBookingDate().toString());
        map.put("purpose", dto.getPurpose());
        map.put("counterparty", dto.getCounterparty());
        map.put("amount", dto.getAmount() == null ? null : dto.getAmount().toString());
        if (dto instanceof IngV1TransactionDTO ing) {
            map.put(
                    "valueDate",
                    ing.getValueDate() == null ? null : ing.getValueDate().toString());
            map.put("bookingText", ing.getBookingText());
            map.put(
                    "balance",
                    ing.getBalance() == null ? null : ing.getBalance().toString());
            map.put("balanceCurrency", ing.getBalanceCurrency());
            map.put("amountCurrency", ing.getAmountCurrency());
        } else if (dto instanceof CamtV8TransactionDTO camt) {
            map.put(
                    "valueDate",
                    camt.getValueDate() == null ? null : camt.getValueDate().toString());
            map.put("bookingText", camt.getBookingText());
            map.put("creditorId", camt.getCreditorId());
            map.put("mandateReference", camt.getMandateReference());
            map.put("customerReference", camt.getCustomerReference());
            map.put("collectorReference", camt.getCollectorReference());
            map.put("directDebitOriginalAmount", camt.getDirectDebitOriginalAmount());
            map.put("refundFee", camt.getRefundFee());
            map.put("bic", camt.getBic());
            map.put("currency", camt.getCurrency());
            map.put("info", camt.getInfo());
        }
        return map;
    }
}
