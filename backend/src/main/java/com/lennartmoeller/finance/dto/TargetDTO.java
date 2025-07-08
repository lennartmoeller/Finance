package com.lennartmoeller.finance.dto;

import java.time.LocalDate;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@Setter
public class TargetDTO {
    private @Nullable Long id;
    private Long categoryId;
    private LocalDate start;
    private @Nullable LocalDate end;
    private @Nullable Long amount;
}
