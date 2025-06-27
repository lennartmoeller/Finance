package com.lennartmoeller.finance.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.junit.jupiter.api.Test;

class CellStatsDTOTest {
    @Test
    void testEmpty() {
        CellStatsDTO dto = CellStatsDTO.empty();
        assertEquals(0.0, dto.getSurplus().getRaw());
        assertEquals(0.0, dto.getSurplus().getSmoothed());
        assertEquals(0.0, dto.getTarget());
        assertNull(dto.getPerformance());
    }

    @Test
    void testAdd() {
        CellStatsDTO a = new CellStatsDTO();
        StatsMetricDTO m1 = new StatsMetricDTO();
        m1.setRaw(1.0);
        m1.setSmoothed(2.0);
        a.setSurplus(m1);
        a.setTarget(3.0);
        PerformanceDTO p1 = new PerformanceDTO();
        p1.setRaw(0.5);
        p1.setSmoothed(0.6);
        a.setPerformance(p1);

        CellStatsDTO b = new CellStatsDTO();
        StatsMetricDTO m2 = new StatsMetricDTO();
        m2.setRaw(4.0);
        m2.setSmoothed(5.0);
        b.setSurplus(m2);
        b.setTarget(1.0);
        PerformanceDTO p2 = new PerformanceDTO();
        p2.setRaw(0.7);
        p2.setSmoothed(0.8);
        b.setPerformance(p2);

        CellStatsDTO result = CellStatsDTO.add(List.of(a, b));
        assertEquals(5.0, result.getSurplus().getRaw());
        assertEquals(7.0, result.getSurplus().getSmoothed());
        assertEquals(4.0, result.getTarget());
        assertNotNull(result.getPerformance());
        assertEquals(0.6, result.getPerformance().getRaw());
        assertEquals(0.7, result.getPerformance().getSmoothed());
    }

    @Test
    void testDeviation() {
        CellStatsDTO dto = new CellStatsDTO();
        StatsMetricDTO m = new StatsMetricDTO();
        m.setRaw(3.0);
        m.setSmoothed(4.0);
        dto.setSurplus(m);
        dto.setTarget(2.0);

        StatsMetricDTO dev = dto.getDeviation();
        assertEquals(5.0, dev.getRaw());
        assertEquals(6.0, dev.getSmoothed());
    }

    @Test
    void testCalculatePerformance() {
        CellStatsDTO dto = new CellStatsDTO();
        StatsMetricDTO m = new StatsMetricDTO();
        m.setRaw(4.0);
        m.setSmoothed(6.0);
        dto.setSurplus(m);

        ImmutableTriple<Double, Double, Double> rawBounds = new ImmutableTriple<>(0.0, 5.0, 10.0);
        ImmutableTriple<Double, Double, Double> smoothedBounds = new ImmutableTriple<>(0.0, 5.0, 10.0);
        dto.calculatePerformance(rawBounds, smoothedBounds);

        assertNotNull(dto.getPerformance());
        assertEquals(0.4, dto.getPerformance().getRaw(), 1e-9);
        assertEquals(0.6, dto.getPerformance().getSmoothed(), 1e-9);
    }
}
