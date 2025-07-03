package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import com.lennartmoeller.finance.model.BankTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface BankTransactionMapper {

    BankTransactionDTO toDto(BankTransaction entity);

    BankTransaction toEntity(BankTransactionDTO dto);

    @Mapping(target = "bank", constant = "ING_V1")
    BankTransaction toEntity(IngV1TransactionDTO dto);

    @Mapping(target = "bank", constant = "CAMT_V8")
    BankTransaction toEntity(CamtV8TransactionDTO dto);
}
