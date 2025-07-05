package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.TransactionLinkSuggestionDTO;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.model.TransactionLinkSuggestion;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface TransactionLinkSuggestionMapper {
    @Mapping(source = "bankTransaction.id", target = "bankTransactionId")
    @Mapping(source = "transaction.id", target = "transactionId")
    TransactionLinkSuggestionDTO toDto(TransactionLinkSuggestion entity);

    @Mapping(
            target = "bankTransaction",
            source = "bankTransactionId",
            qualifiedByName = "mapBankTransactionIdToBankTransaction")
    @Mapping(target = "transaction", source = "transactionId", qualifiedByName = "mapTransactionIdToTransaction")
    TransactionLinkSuggestion toEntity(
            TransactionLinkSuggestionDTO dto,
            @Context BankTransactionRepository bankTransactionRepository,
            @Context TransactionRepository transactionRepository);

    @Named("mapBankTransactionIdToBankTransaction")
    default BankTransaction mapBankTransactionIdToBankTransaction(
            Long id, @Context BankTransactionRepository repository) {
        return id != null ? repository.findById(id).orElse(null) : null;
    }

    @Named("mapTransactionIdToTransaction")
    default Transaction mapTransactionIdToTransaction(Long id, @Context TransactionRepository repository) {
        return id != null ? repository.findById(id).orElse(null) : null;
    }
}
