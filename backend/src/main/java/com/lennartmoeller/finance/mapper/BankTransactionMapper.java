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

    @Mapping(source = "account.iban", target = "iban")
    BankTransactionDTO toDto(BankTransaction entity);

    @Mapping(target = "account", source = "iban", qualifiedByName = "mapIbanToAccount")
    BankTransaction toEntity(BankTransactionDTO dto, @Context AccountRepository accountRepository);

    @Mapping(target = "bank", constant = "ING_V1")
    @Mapping(target = "account", source = "iban", qualifiedByName = "mapIbanToAccount")
    BankTransaction toEntity(IngV1TransactionDTO dto, @Context AccountRepository accountRepository);

    @Mapping(target = "bank", constant = "CAMT_V8")
    @Mapping(target = "account", source = "iban", qualifiedByName = "mapIbanToAccount")
    BankTransaction toEntity(CamtV8TransactionDTO dto, @Context AccountRepository accountRepository);

    @Named("mapIbanToAccount")
    default Account mapIbanToAccount(String iban, @Context AccountRepository repository) {
        return iban != null ? repository.findByIban(iban).orElse(null) : null;
    }
}
