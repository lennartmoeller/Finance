package com.lennartmoeller.finance.dto;

import java.time.LocalDate;
import javax.annotation.Nonnull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DailySavingStatsDTO {
    @Nonnull
    private LocalDate date;

    @Nonnull
    private StatsMetricDTO balance;

    @Nonnull
    private StatsMetricDTO target;
}
