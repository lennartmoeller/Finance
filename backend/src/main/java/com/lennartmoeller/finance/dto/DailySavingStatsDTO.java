package com.lennartmoeller.finance.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@Setter
public class DailySavingStatsDTO {

    private LocalDate date;
    private StatsMetricDTO balance;
    private StatsMetricDTO target;
}
