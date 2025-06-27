package com.lennartmoeller.finance.util.smoother;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lennartmoeller.finance.dto.StatsMetricDTO;
import com.lennartmoeller.finance.model.CategorySmoothType;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class SmootherDailyTest {

    @Test
    void testAddDaily() {
        SmootherDaily smoother = new SmootherDaily();
        LocalDate date = LocalDate.of(2021, 1, 15);
        smoother.add(date, CategorySmoothType.DAILY, 100L);

        // For DAILY smoothing, the date range is just [date, date]
        StatsMetricDTO metric = smoother.get(date);
        // Expect raw = 100 and smoothed = 100 on the same day
        assertEquals(100.0, metric.getRaw());
        assertEquals(100.0, metric.getSmoothed());
    }

    @Test
    void testAddMonthly() {
        SmootherDaily smoother = new SmootherDaily();
        LocalDate date = LocalDate.of(2021, 1, 15);
        // For MONTHLY smoothing, the range spans the entire month (January has 31 days)
        smoother.add(date, CategorySmoothType.MONTHLY, 3100L);

        // Raw is added only to the original date key (2021-01-15)
        StatsMetricDTO metricRaw = smoother.get(date);
        assertEquals(3100.0, metricRaw.getRaw());
        // Each day in January should get an equal portion: 3100/31 = 100 per day.
        StatsMetricDTO metricJan1 = smoother.get(LocalDate.of(2021, 1, 1));
        assertEquals(100.0, metricJan1.getSmoothed(), 0.0001);
    }

    @Test
    void testAddQuarterYearly() {
        SmootherDaily smoother = new SmootherDaily();
        LocalDate date = LocalDate.of(2021, 2, 15);
        // For QUARTER_YEARLY smoothing, the range covers Q1 (Jan 1 - Mar 31, 2021).
        // Q1 of 2021 has 90 days.
        smoother.add(date, CategorySmoothType.QUARTER_YEARLY, 9000L);
        StatsMetricDTO metric = smoother.get(date);
        // Raw value should be 9000, and each day gets 9000/90 = 100.
        assertEquals(9000.0, metric.getRaw());
        assertEquals(100.0, metric.getSmoothed(), 0.0001);
    }

    @Test
    void testAddHalfYearly() {
        SmootherDaily smoother = new SmootherDaily();
        LocalDate date = LocalDate.of(2021, 4, 15); // belongs to H1 (Jan 1 - Jun 30)
        // H1 in 2021 has 181 days.
        smoother.add(date, CategorySmoothType.HALF_YEARLY, 18100L);

        // Raw value on input date is 18100.
        StatsMetricDTO metricRaw = smoother.get(date);
        assertEquals(18100.0, metricRaw.getRaw());
        // Check a day within H1 (e.g., Jan 1) gets 18100/181 = 100.
        StatsMetricDTO metricJan1 = smoother.get(LocalDate.of(2021, 1, 1));
        assertEquals(100.0, metricJan1.getSmoothed(), 0.0001);
        // A day outside H1 (e.g., July 1) should not have any smoothed value.
        StatsMetricDTO metricJul1 = smoother.get(LocalDate.of(2021, 7, 1));
        assertEquals(0.0, metricJul1.getSmoothed(), 0.0001);
    }

    @Test
    void testAddYearly() {
        SmootherDaily smoother = new SmootherDaily();
        LocalDate date = LocalDate.of(2021, 8, 15);
        // For YEARLY smoothing, the range covers the entire year (365 days for 2021).
        smoother.add(date, CategorySmoothType.YEARLY, 36500L);

        // Raw value on input date is 36500.
        StatsMetricDTO metricRaw = smoother.get(date);
        assertEquals(36500.0, metricRaw.getRaw());
        // Check a day in the year (e.g., Jan 1) gets 36500/365 = 100.
        StatsMetricDTO metricJan1 = smoother.get(LocalDate.of(2021, 1, 1));
        assertEquals(100.0, metricJan1.getSmoothed(), 0.0001);
        // Also check Dec 31.
        StatsMetricDTO metricDec31 = smoother.get(LocalDate.of(2021, 12, 31));
        assertEquals(100.0, metricDec31.getSmoothed(), 0.0001);
    }

    @Test
    void testAccumulateAdditionsDaily() {
        SmootherDaily smoother = new SmootherDaily();
        LocalDate date = LocalDate.of(2021, 1, 15);
        // Add twice for DAILY smoothing.
        smoother.add(date, CategorySmoothType.DAILY, 50L);
        smoother.add(date, CategorySmoothType.DAILY, 70L);

        StatsMetricDTO metric = smoother.get(date);
        // Raw values accumulate: 50 + 70 = 120.
        assertEquals(120.0, metric.getRaw());
        // For DAILY smoothing, smoothed equals the raw value on the same day.
        assertEquals(120.0, metric.getSmoothed(), 0.0001);
    }
}
