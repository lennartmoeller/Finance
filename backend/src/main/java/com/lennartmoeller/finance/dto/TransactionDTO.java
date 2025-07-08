package com.lennartmoeller.finance.dto;

import java.time.LocalDate;
import javax.annotation.Nullable;
import lombok.Data;

@Data
public class TransactionDTO {
    private @Nullable Long id;
    private Long accountId;
    private Long categoryId;
    private LocalDate date;
    private Long amount;
    private String description;
    private Boolean pinned;
}
