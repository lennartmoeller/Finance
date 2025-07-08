package com.lennartmoeller.finance.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class DailySavingStatsDTO {
    private LocalDate date;
    private StatsMetricDTO balance;
    private StatsMetricDTO target;
}
