package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CamtV8TransactionMapper extends BankTransactionMapper {
    @Override
    default Map<String, String> toDataMap(BankTransactionDTO dto) {
        Map<String, String> map = BankTransactionMapper.super.toDataMap(dto);
        CamtV8TransactionDTO camtDto = (CamtV8TransactionDTO) dto;
        map.put(
                "valueDate",
                camtDto.getValueDate() == null ? null : camtDto.getValueDate().toString());
        map.put("bookingText", camtDto.getBookingText());
        map.put("creditorId", camtDto.getCreditorId());
        map.put("mandateReference", camtDto.getMandateReference());
        map.put("customerReference", camtDto.getCustomerReference());
        map.put("collectorReference", camtDto.getCollectorReference());
        map.put("directDebitOriginalAmount", camtDto.getDirectDebitOriginalAmount());
        map.put("refundFee", camtDto.getRefundFee());
        map.put("bic", camtDto.getBic());
        map.put("currency", camtDto.getCurrency());
        map.put("info", camtDto.getInfo());
        return map;
    }
}
