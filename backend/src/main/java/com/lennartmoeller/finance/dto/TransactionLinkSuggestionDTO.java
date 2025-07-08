package com.lennartmoeller.finance.dto;

import com.lennartmoeller.finance.model.TransactionLinkState;
import javax.annotation.Nullable;
import lombok.Data;

@Data
public class TransactionLinkSuggestionDTO {
    private @Nullable Long id;
    private Long bankTransactionId;
    private Long transactionId;
    private Double probability;
    private TransactionLinkState linkState;
}
