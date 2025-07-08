package com.lennartmoeller.finance.dto;

import java.time.LocalDate;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@Setter
public class TransactionDTO {
    private @Nullable Long id;
    private Long accountId;
    private Long categoryId;
    private LocalDate date;
    private Long amount;
    private String description;
    private Boolean pinned;
}
