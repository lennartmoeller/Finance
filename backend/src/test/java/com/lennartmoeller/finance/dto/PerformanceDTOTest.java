package com.lennartmoeller.finance.dto;

import com.lennartmoeller.finance.model.TransactionType;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PerformanceDTOTest {

    @Test
    void testMean() {
        PerformanceDTO a = new PerformanceDTO();
        a.setRaw(0.2);
        a.setSmoothed(0.4);
        PerformanceDTO b = new PerformanceDTO();
        b.setRaw(0.6);
        b.setSmoothed(0.8);

        PerformanceDTO mean = PerformanceDTO.mean(List.of(a, b));
        assertEquals(0.4, mean.getRaw(), 1e-9);
        assertEquals(0.6, mean.getSmoothed(), 1e-9);
    }

    @Test
    void testCalculateBoundsIncomeAndExpense() {
        DescriptiveStatistics ds = new DescriptiveStatistics();
        ds.addValue(-1);
        ds.addValue(0);
        ds.addValue(1);

        ImmutableTriple<Double, Double, Double> income = PerformanceDTO.calculateBounds(ds, TransactionType.INCOME);
        assertEquals(0.0, income.left);
        assertEquals(0.0, income.middle);
        assertEquals(1.0, income.right);

        ImmutableTriple<Double, Double, Double> expense = PerformanceDTO.calculateBounds(ds, TransactionType.EXPENSE);
        assertEquals(-1.0, expense.left);
        assertEquals(0.0, expense.middle);
        assertEquals(0.0, expense.right);
    }

    @Test
    void testCalculate() {
        ImmutableTriple<Double, Double, Double> bounds = new ImmutableTriple<>(0.0, 5.0, 10.0);
        PerformanceDTO perf = PerformanceDTO.calculate(4.0, bounds, 6.0, bounds);
        assertEquals(0.4, perf.getRaw(), 1e-9);
        assertEquals(0.6, perf.getSmoothed(), 1e-9);
    }

    @Test
    void testCalculateEdgeCases() {
        ImmutableTriple<Double, Double, Double> bounds = new ImmutableTriple<>(0.0, 5.0, 10.0);

        PerformanceDTO high = PerformanceDTO.calculate(15.0, bounds, 15.0, bounds);
        assertEquals(1.0, high.getRaw());
        assertEquals(1.0, high.getSmoothed());

        PerformanceDTO low = PerformanceDTO.calculate(-1.0, bounds, -1.0, bounds);
        assertEquals(0.0, low.getRaw());
        assertEquals(0.0, low.getSmoothed());

        PerformanceDTO mid = PerformanceDTO.calculate(5.0, bounds, 5.0, bounds);
        assertEquals(0.5, mid.getRaw());
        assertEquals(0.5, mid.getSmoothed());
    }
}
