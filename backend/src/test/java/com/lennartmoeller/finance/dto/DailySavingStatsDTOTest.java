package com.lennartmoeller.finance.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DailySavingStatsDTOTest {
    @Test
    void gettersAndSetters() {
        DailySavingStatsDTO dto = new DailySavingStatsDTO();
        LocalDate date = LocalDate.of(2024, 1, 1);
        StatsMetricDTO bal = new StatsMetricDTO();
        StatsMetricDTO target = new StatsMetricDTO();
        dto.setDate(date);
        dto.setBalance(bal);
        dto.setTarget(target);

        assertEquals(date, dto.getDate());
        assertSame(bal, dto.getBalance());
        assertSame(target, dto.getTarget());
    }
}
