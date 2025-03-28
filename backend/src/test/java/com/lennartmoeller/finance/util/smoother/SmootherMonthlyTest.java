package com.lennartmoeller.finance.util.smoother;

import com.lennartmoeller.finance.dto.StatsMetricDTO;
import com.lennartmoeller.finance.model.CategorySmoothType;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SmootherMonthlyTest {

	@Test
	void testAddMonthly() {
		SmootherMonthly smoother = new SmootherMonthly();
		YearMonth ym = YearMonth.of(2021, 5);
		// For MONTHLY smoothing, the range is the single month.
		smoother.add(ym, CategorySmoothType.MONTHLY, 500L);
		// Raw for May 2021 should be 500; since there is only one month, smoothed is also 500.
		StatsMetricDTO metric = smoother.get(ym);
		assertEquals(500.0, metric.getRaw());
		assertEquals(500.0, metric.getSmoothed(), 0.0001);
	}

	@Test
	void testAddYearly() {
		SmootherMonthly smoother = new SmootherMonthly();
		YearMonth ym = YearMonth.of(2021, 1);
		// For YEARLY smoothing, the range covers all 12 months.
		smoother.add(ym, CategorySmoothType.YEARLY, 1200L);
		// Raw value stored on the original key (January 2021) is 1200.
		StatsMetricDTO metricJan = smoother.get(YearMonth.of(2021, 1));
		assertEquals(1200.0, metricJan.getRaw());
		// Each month in the year gets 1200/12 = 100.
		assertEquals(100.0, smoother.get(YearMonth.of(2021, 1)).getSmoothed(), 0.0001);
	}

	@Test
	void testAddQuarterYearly() {
		SmootherMonthly smoother = new SmootherMonthly();
		YearMonth ym = YearMonth.of(2021, 5); // May is in Q2 (April, May, June)
		// For QUARTER_YEARLY smoothing, the range covers 3 months.
		smoother.add(ym, CategorySmoothType.QUARTER_YEARLY, 300L);
		// Raw value stored on the original key (May 2021) is 300.
		StatsMetricDTO metricRaw = smoother.get(YearMonth.of(2021, 5));
		assertEquals(300.0, metricRaw.getRaw());
		// Each month in Q2 should receive 300/3 = 100 as the smoothed amount.
		assertEquals(100.0, smoother.get(YearMonth.of(2021, 4)).getSmoothed(), 0.0001);
		assertEquals(100.0, smoother.get(YearMonth.of(2021, 5)).getSmoothed(), 0.0001);
		assertEquals(100.0, smoother.get(YearMonth.of(2021, 6)).getSmoothed(), 0.0001);
	}

	@Test
	void testAddHalfYearly() {
		SmootherMonthly smoother = new SmootherMonthly();
		YearMonth ym = YearMonth.of(2021, 3); // March belongs to H1 (January to June)
		// For HALF_YEARLY smoothing, the range covers 6 months.
		smoother.add(ym, CategorySmoothType.HALF_YEARLY, 600L);
		// Raw value stored on the original key (March 2021) is 600.
		StatsMetricDTO metricRaw = smoother.get(YearMonth.of(2021, 3));
		assertEquals(600.0, metricRaw.getRaw());
		// Each month in H1 gets 600/6 = 100.
		YearMonth[] months = {YearMonth.of(2021, 1), YearMonth.of(2021, 2), YearMonth.of(2021, 3), YearMonth.of(2021, 4), YearMonth.of(2021, 5), YearMonth.of(2021, 6)};
		for (YearMonth month : months) {
			assertEquals(100.0, smoother.get(month).getSmoothed(), 0.0001);
		}
	}

	@Test
	void testAccumulateAdditionsMonthly() {
		SmootherMonthly smoother = new SmootherMonthly();
		YearMonth ym = YearMonth.of(2021, 5);
		// Add two additions with MONTHLY smoothing.
		smoother.add(ym, CategorySmoothType.MONTHLY, 200L);
		smoother.add(ym, CategorySmoothType.MONTHLY, 300L);
		StatsMetricDTO metric = smoother.get(ym);
		// Raw should accumulate: 200 + 300 = 500.
		assertEquals(500.0, metric.getRaw());
		// Since the range is a single month, smoothed equals the raw value.
		assertEquals(500.0, metric.getSmoothed(), 0.0001);
	}

}
