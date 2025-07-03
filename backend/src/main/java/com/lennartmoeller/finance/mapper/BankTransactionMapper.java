package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.repository.AccountRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface BankTransactionMapper {

    @Mapping(source = "account.id", target = "accountId")
    BankTransactionDTO toDto(BankTransaction entity);

    @Mapping(target = "account", source = "accountId", qualifiedByName = "mapAccount")
    BankTransaction toEntity(BankTransactionDTO dto, @Context AccountRepository accountRepository);

    @Mapping(target = "bank", constant = "ING_V1")
    @Mapping(target = "account", source = "accountId", qualifiedByName = "mapAccount")
    BankTransaction toEntity(IngV1TransactionDTO dto, @Context AccountRepository accountRepository);

    @Mapping(target = "bank", constant = "CAMT_V8")
    @Mapping(target = "account", source = "accountId", qualifiedByName = "mapAccount")
    BankTransaction toEntity(CamtV8TransactionDTO dto, @Context AccountRepository accountRepository);

    @Named("mapAccount")
    default Account mapAccount(Long id, @Context AccountRepository repository) {
        return id != null ? repository.findById(id).orElse(null) : null;
    }
}
