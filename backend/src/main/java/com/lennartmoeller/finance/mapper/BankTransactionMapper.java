package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.csv.BankCsvParser;
import com.lennartmoeller.finance.csv.CamtV8CsvParser;
import com.lennartmoeller.finance.csv.IngV1CsvParser;
import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.util.EuroParser;
import java.time.LocalDate;
import java.util.List;
import javax.annotation.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BankTransactionMapper {
    default BankTransactionDTO toDto(@Nullable BankTransaction entity) {
        if (entity == null) {
            return null;
        }
        return switch (entity.getBank()) {
            case ING_V1 -> mapIngV1(entity);
            case CAMT_V8 -> mapCamtV8(entity);
        };
    }

    private IngV1TransactionDTO mapIngV1(BankTransaction entity) {
        IngV1TransactionDTO dto = mapCommonFields(entity, new IngV1TransactionDTO());
        List<String> data = BankCsvParser.parseLine(entity.getData());
        dto.setValueDate(LocalDate.parse(data.get(1), IngV1CsvParser.DATE));
        dto.setBookingText(data.get(3));
        String amountStr = data.get(5);
        dto.setBalance(EuroParser.parseToCents(amountStr)
                .orElseThrow(() -> new IllegalArgumentException("Invalid amount format: " + amountStr)));
        dto.setBalanceCurrency(data.get(6));
        dto.setAmountCurrency(data.get(8));
        return dto;
    }

    private CamtV8TransactionDTO mapCamtV8(BankTransaction entity) {
        CamtV8TransactionDTO dto = mapCommonFields(entity, new CamtV8TransactionDTO());
        List<String> data = BankCsvParser.parseLine(entity.getData());
        dto.setValueDate(LocalDate.parse(data.get(2), CamtV8CsvParser.DATE));
        dto.setBookingText(data.get(3));
        dto.setCreditorId(data.get(5));
        dto.setMandateReference(data.get(6));
        dto.setCustomerReference(data.get(7));
        dto.setCollectorReference(data.get(8));
        dto.setDirectDebitOriginalAmount(data.get(9));
        dto.setRefundFee(data.get(10));
        dto.setBic(data.get(13));
        dto.setCurrency(data.get(15));
        dto.setInfo(data.get(16));
        return dto;
    }

    private <T extends BankTransactionDTO> T mapCommonFields(BankTransaction entity, T dto) {
        dto.setId(entity.getId());
        dto.setBank(entity.getBank());
        dto.setAccountId(entity.getAccount().getId());
        dto.setBookingDate(entity.getBookingDate());
        dto.setPurpose(entity.getPurpose());
        dto.setCounterparty(entity.getCounterparty());
        dto.setAmount(entity.getAmount());
        return dto;
    }
}
