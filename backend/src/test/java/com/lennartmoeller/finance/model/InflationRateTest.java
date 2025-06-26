package com.lennartmoeller.finance.model;

import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

class InflationRateTest {

        @Test
        void testGettersAndSetters() {
                InflationRate rate = new InflationRate();
                rate.setYearMonth(YearMonth.of(2021, 1));
                rate.setRate(2.5);

                assertEquals(YearMonth.of(2021, 1), rate.getYearMonth());
                assertEquals(2.5, rate.getRate());
        }

        @Test
        void testEqualsAndHashCode() {
                InflationRate r1 = new InflationRate();
                r1.setId(1L);
                r1.setRate(3.0);

                InflationRate r2 = new InflationRate();
                r2.setId(1L);
                r2.setRate(4.0);

                assertEquals(r1, r2);
                assertEquals(r1.hashCode(), r2.hashCode());

                r2.setId(2L);
                assertNotEquals(r1, r2);
        }
}
