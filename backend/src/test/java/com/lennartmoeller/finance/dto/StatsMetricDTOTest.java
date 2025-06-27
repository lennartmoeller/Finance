package com.lennartmoeller.finance.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class StatsMetricDTOTest {

    @Test
    void testEmpty() {
        StatsMetricDTO dto = StatsMetricDTO.empty();
        assertEquals(0.0, dto.getRaw());
        assertEquals(0.0, dto.getSmoothed());
    }

    @Test
    void testAdd() {
        StatsMetricDTO a = new StatsMetricDTO();
        a.setRaw(1.0);
        a.setSmoothed(2.0);

        StatsMetricDTO b = new StatsMetricDTO();
        b.setRaw(3.0);
        b.setSmoothed(4.0);

        StatsMetricDTO result = StatsMetricDTO.add(List.of(a, b));
        assertEquals(4.0, result.getRaw());
        assertEquals(6.0, result.getSmoothed());
    }

    @Test
    void testMultiply() {
        StatsMetricDTO original = new StatsMetricDTO();
        original.setRaw(5.0);
        original.setSmoothed(7.0);

        StatsMetricDTO result = StatsMetricDTO.multiply(original, 2.0);
        assertEquals(10.0, result.getRaw());
        assertEquals(14.0, result.getSmoothed());
        // ensure original not modified
        assertEquals(5.0, original.getRaw());
        assertEquals(7.0, original.getSmoothed());
    }

    @Test
    void testMean() {
        StatsMetricDTO a = new StatsMetricDTO();
        a.setRaw(2.0);
        a.setSmoothed(4.0);

        StatsMetricDTO b = new StatsMetricDTO();
        b.setRaw(6.0);
        b.setSmoothed(8.0);

        StatsMetricDTO mean = StatsMetricDTO.mean(List.of(a, b));
        assertEquals(4.0, mean.getRaw());
        assertEquals(6.0, mean.getSmoothed());
    }

    @Test
    void testMeanEmptyList() {
        StatsMetricDTO mean = StatsMetricDTO.mean(List.of());
        assertEquals(0.0, mean.getRaw());
        assertEquals(0.0, mean.getSmoothed());
    }
}
