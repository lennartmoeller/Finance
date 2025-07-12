package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IngV1TransactionMapper extends BankTransactionMapper {
    @Override
    default Map<String, String> toDataMap(BankTransactionDTO dto) {
        Map<String, String> map = BankTransactionMapper.super.toDataMap(dto);
        IngV1TransactionDTO ingDto = (IngV1TransactionDTO) dto;
        map.put(
                "valueDate",
                ingDto.getValueDate() == null ? null : ingDto.getValueDate().toString());
        map.put("bookingText", ingDto.getBookingText());
        map.put(
                "balance",
                ingDto.getBalance() == null ? null : ingDto.getBalance().toString());
        map.put("balanceCurrency", ingDto.getBalanceCurrency());
        map.put("amountCurrency", ingDto.getAmountCurrency());
        return map;
    }
}
