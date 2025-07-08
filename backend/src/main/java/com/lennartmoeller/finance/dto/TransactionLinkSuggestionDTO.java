package com.lennartmoeller.finance.dto;

import com.lennartmoeller.finance.model.TransactionLinkState;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@Setter
public class TransactionLinkSuggestionDTO {
    private @Nullable Long id;
    private Long bankTransactionId;
    private Long transactionId;
    private Double probability;
    private TransactionLinkState linkState;
}
