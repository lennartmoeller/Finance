package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BankTransactionMapper {
    @Mapping(source = "account.iban", target = "iban")
    IngV1TransactionDTO toIngDto(BankTransaction entity);

    @Mapping(source = "account.iban", target = "iban")
    CamtV8TransactionDTO toCamtDto(BankTransaction entity);

    default BankTransactionDTO toDto(BankTransaction entity) {
        if (entity == null) {
            return null;
        }
        if (entity.getBank() == null) {
            return toIngDto(entity);
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
}
