package com.lennartmoeller.finance.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lennartmoeller.finance.model.TransactionType;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MonthlyCategoryStatsDTOTest {
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
        MonthlyCategoryStatsDTO dto = MonthlyCategoryStatsDTO.empty();
        assertNull(dto.getStartDate());
        assertNull(dto.getEndDate());
        for (TransactionType t : TransactionType.values()) {
            assertTrue(dto.getStats().containsKey(t));
            assertTrue(dto.getStats().get(t).getCategoryStats().isEmpty());
        }
    }

    @Test
    void testGetTotalStats() {
        YearMonth m1 = YearMonth.of(2023, 1);
        YearMonth m2 = YearMonth.of(2023, 2);
        RowStatsDTO row1 = new RowStatsDTO(Map.of(m1, cell(1), m2, cell(2)));
        RowStatsDTO row2 = new RowStatsDTO(Map.of(m1, cell(3), m2, cell(4)));

        CategoryStatsDTO c1 = new CategoryStatsDTO();
        c1.setStats(row1);
        CategoryStatsDTO c2 = new CategoryStatsDTO();
        c2.setStats(row2);

        TransactionTypeStatsDTO income = new TransactionTypeStatsDTO(List.of(c1), null);
        TransactionTypeStatsDTO expense = new TransactionTypeStatsDTO(List.of(c2), null);

        MonthlyCategoryStatsDTO dto = new MonthlyCategoryStatsDTO();
        dto.setStats(Map.of(
                TransactionType.INCOME, income,
                TransactionType.EXPENSE, expense));
        RowStatsDTO total = dto.getTotalStats();

        assertEquals(2, total.getMonthly().size());
        assertEquals(4.0, total.getMonthly().get(m1).getSurplus().getRaw());
        assertEquals(6.0, total.getMonthly().get(m2).getSurplus().getRaw());
    }
}
