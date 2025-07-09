package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface BankTransactionMapper {
    @Mapping(source = "account.iban", target = "iban")
    BankTransactionDTO toDto(BankTransaction entity);

    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "bank", constant = "ING_V1")
    @Mapping(target = "account", source = "account")
    BankTransaction toEntity(IngV1TransactionDTO dto, Account account);

    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "bank", constant = "CAMT_V8")
    @Mapping(target = "account", source = "account")
    BankTransaction toEntity(CamtV8TransactionDTO dto, Account account);
}
