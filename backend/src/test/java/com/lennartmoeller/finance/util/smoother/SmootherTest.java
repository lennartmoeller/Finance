package com.lennartmoeller.finance.util.smoother;

import com.lennartmoeller.finance.dto.StatsMetricDTO;
import com.lennartmoeller.finance.model.CategorySmoothType;
import com.lennartmoeller.finance.util.DateRange;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SmootherTest {

    private static class TestSmoother extends Smoother<String> {
        @Override
        public void add(String key, CategorySmoothType smoothType, Long amount) {
            // not needed for these tests
        }

        @Override
        public StatsMetricDTO get(String key) {
            return data.getOrDefault(key, StatsMetricDTO.empty());
        }

        @Override
        protected DateRange getDateRange(String key, CategorySmoothType smoothType) {
            return null;
        }

        void addRaw(String key, double amount) {
            addRawToData(key, amount);
        }

        void addSmoothed(String key, double amount) {
            addSmoothedToData(key, amount);
        }
    }

    @Test
    void testAddRawToDataAccumulates() {
        TestSmoother smoother = new TestSmoother();
        smoother.addRaw("a", 5);
        smoother.addRaw("a", 3);
        StatsMetricDTO result = smoother.get("a");
        assertEquals(8.0, result.getRaw());
        assertEquals(0.0, result.getSmoothed());
    }

    @Test
    void testAddSmoothedToDataAccumulates() {
        TestSmoother smoother = new TestSmoother();
        smoother.addSmoothed("b", 2);
        smoother.addSmoothed("b", 7);
        StatsMetricDTO result = smoother.get("b");
        assertEquals(0.0, result.getRaw());
        assertEquals(9.0, result.getSmoothed());
    }

    @Test
    void testAddRawAndSmoothed() {
        TestSmoother smoother = new TestSmoother();
        smoother.addRaw("c", 4);
        smoother.addSmoothed("c", 1.5);
        StatsMetricDTO result = smoother.get("c");
        assertEquals(4.0, result.getRaw());
        assertEquals(1.5, result.getSmoothed());
    }
}
