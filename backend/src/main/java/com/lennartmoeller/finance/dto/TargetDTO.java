package com.lennartmoeller.finance.dto;

import java.time.LocalDate;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TargetDTO {
    @Nullable
    private Long id;

    @Nullable
    private Long categoryId;

    @Nullable
    private LocalDate start;

    @Nullable
    private LocalDate end;

    @Nullable
    private Long amount;
}
