package com.lennartmoeller.finance.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.lennartmoeller.finance.util.DateRange;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class TransactionTypeStatsDTOTest {

    private static CellStatsDTO cell(double raw) {
        StatsMetricDTO m = new StatsMetricDTO();
        m.setRaw(raw);
        m.setSmoothed(raw);
        CellStatsDTO c = new CellStatsDTO();
        c.setSurplus(m);
        c.setTarget(0.0);
        PerformanceDTO p = new PerformanceDTO();
        c.setPerformance(p);
        return c;
    }

    @Test
    void testEmpty() {
        TransactionTypeStatsDTO dto = TransactionTypeStatsDTO.empty(null);
        assertTrue(dto.getCategoryStats().isEmpty());
        assertNull(dto.getDateRange());
    }

    @Test
    void testGetTotalStatsNullRange() {
        TransactionTypeStatsDTO dto = new TransactionTypeStatsDTO(List.of(), null);
        RowStatsDTO row = dto.getTotalStats();
        assertTrue(row.getMonthly().isEmpty());
    }

    @Test
    void testGetTotalStatsEmptyCategories() {
        DateRange range = new DateRange(YearMonth.of(2023, 1), YearMonth.of(2023, 2));
        TransactionTypeStatsDTO dto = new TransactionTypeStatsDTO(List.of(), range);
        RowStatsDTO row = dto.getTotalStats();
        assertEquals(2, row.getMonthly().size());
        assertTrue(
                row.getMonthly().values().stream().allMatch(c -> c.getSurplus().getRaw() == 0.0));
    }

    @Test
    void testGetTotalStats() {
        YearMonth m1 = YearMonth.of(2023, 1);
        YearMonth m2 = YearMonth.of(2023, 2);
        CategoryStatsDTO c1 = new CategoryStatsDTO();
        c1.setStats(new RowStatsDTO(Map.of(m1, cell(1), m2, cell(2))));
        CategoryStatsDTO c2 = new CategoryStatsDTO();
        c2.setStats(new RowStatsDTO(Map.of(m1, cell(3), m2, cell(4))));
        DateRange range = new DateRange(m1, m2);
        TransactionTypeStatsDTO dto = new TransactionTypeStatsDTO(List.of(c1, c2), range);
        RowStatsDTO row = dto.getTotalStats();
        assertEquals(2, row.getMonthly().size());
        assertEquals(4.0, row.getMonthly().get(m1).getSurplus().getRaw());
        assertEquals(6.0, row.getMonthly().get(m2).getSurplus().getRaw());
    }
}
