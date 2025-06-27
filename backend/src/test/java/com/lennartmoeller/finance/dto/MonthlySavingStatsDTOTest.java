package com.lennartmoeller.finance.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.YearMonth;
import org.junit.jupiter.api.Test;

class MonthlySavingStatsDTOTest {

    @Test
    void testDeviationsAndImpact() {
        MonthlySavingStatsDTO dto = new MonthlySavingStatsDTO();
        dto.setYearMonth(YearMonth.of(2024, 5));

        StatsMetricDTO change = new StatsMetricDTO();
        change.setRaw(10.0);
        change.setSmoothed(12.0);
        dto.setBalanceChange(change);

        StatsMetricDTO changeTarget = new StatsMetricDTO();
        changeTarget.setRaw(7.0);
        changeTarget.setSmoothed(8.0);
        dto.setBalanceChangeTarget(changeTarget);

        dto.setDeposits(200L);
        dto.setDepositsTarget(150.0);
        dto.setInflationLoss(-5.0);
        dto.setInvestmentRevenue(8.0);

        assertEquals(3.0, dto.getBalanceChangeDeviation().getRaw());
        assertEquals(4.0, dto.getBalanceChangeDeviation().getSmoothed());
        assertEquals(50.0, dto.getDepositsDeviation());
        assertEquals(3.0, dto.getInflationImpact());
    }
}
