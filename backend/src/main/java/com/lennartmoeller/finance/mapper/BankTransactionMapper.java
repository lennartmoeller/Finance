package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import javax.annotation.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BankTransactionMapper {
    @Mapping(source = "account.iban", target = "iban")
    IngV1TransactionDTO toIngDto(BankTransaction entity);

    @Mapping(source = "account.iban", target = "iban")
    CamtV8TransactionDTO toCamtDto(BankTransaction entity);

    default BankTransactionDTO toDto(@Nullable BankTransaction entity) {
        if (entity == null) {
            return null;
        }
        return switch (entity.getBank()) {
            case ING_V1 -> toIngDto(entity);
            case CAMT_V8 -> toCamtDto(entity);
        };
    }

    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "bank", constant = "ING_V1")
    @Mapping(target = "account", source = "account")
    BankTransaction toEntity(IngV1TransactionDTO dto, Account account);

    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "bank", constant = "CAMT_V8")
    @Mapping(target = "account", source = "account")
    BankTransaction toEntity(CamtV8TransactionDTO dto, Account account);

    default BankTransaction toEntity(BankTransactionDTO dto, Account account) {
        if (dto == null) {
            return null;
        }
        return switch (dto) {
            case IngV1TransactionDTO ingV1Dto -> toEntity(ingV1Dto, account);
            case CamtV8TransactionDTO camtV8Dto -> toEntity(camtV8Dto, account);
            default -> throw new IllegalArgumentException("Unsupported BankTransactionDTO type: " + dto.getClass());
        };
    }
}
