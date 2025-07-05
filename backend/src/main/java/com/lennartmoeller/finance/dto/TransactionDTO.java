package com.lennartmoeller.finance.dto;

import java.time.LocalDate;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransactionDTO {
    @Nullable
    private Long id;

    @Nullable
    private Long accountId;

    @Nullable
    private Long categoryId;

    @Nullable
    private LocalDate date;

    @Nullable
    private Long amount;

    @Nullable
    private String description;

    @Nullable
    private Boolean pinned;
}
