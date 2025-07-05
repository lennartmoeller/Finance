package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@Setter
public class TransactionLinkSuggestionDTO {
    private Long id;
    private Long bankTransactionId;
    private Long transactionId;
    private Double probability;
}
