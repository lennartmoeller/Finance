package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import javax.annotation.Nullable;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class BankTransactionMapper {
    @Mapping(source = "account.iban", target = "iban")
    public abstract BankTransactionDTO toDto(@Nullable BankTransaction entity);

    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "account", source = "account")
    public abstract BankTransaction toEntity(BankTransactionDTO dto, Account account);

    @AfterMapping
    protected void fillData(BankTransactionDTO dto, @MappingTarget BankTransaction entity) {
        entity.getData().clear();
        entity.getData().putAll(dto.buildDataMap());
    }

    @ObjectFactory
    protected BankTransactionDTO createDto(@Nullable BankTransaction entity) {
        if (entity == null) {
            return null;
        }
        return switch (entity.getBank()) {
            case ING_V1 -> new IngV1TransactionDTO();
            case CAMT_V8 -> new CamtV8TransactionDTO();
        };
    }
}
