package com.lennartmoeller.finance.util.smoother;

import com.lennartmoeller.finance.dto.StatsMetricDTO;
import com.lennartmoeller.finance.model.CategorySmoothType;
import com.lennartmoeller.finance.util.DateRange;
import com.lennartmoeller.finance.util.YearHalf;
import com.lennartmoeller.finance.util.YearQuarter;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;

public class SmootherDaily extends Smoother<LocalDate> {

	@Override
	public void add(LocalDate date, CategorySmoothType smoothType, Long amount) {
		addRawToData(date, amount);
		DateRange dateRange = getDateRange(date, smoothType);
		long totalDays = dateRange.getDays();
		double dailyPortion = (double) amount / totalDays;
		dateRange.createDateStream().forEach(d -> addSmoothedToData(d, dailyPortion));
	}

	@Override
	public StatsMetricDTO get(LocalDate date) {
		return this.data.getOrDefault(date, StatsMetricDTO.empty());
	}

	@Override
	protected DateRange getDateRange(LocalDate date, CategorySmoothType smoothType) {
		return switch (smoothType) {
			case DAILY -> new DateRange(date, date);
			case MONTHLY -> new DateRange(YearMonth.from(date));
			case QUARTER_YEARLY -> new DateRange(YearQuarter.from(date));
			case HALF_YEARLY -> new DateRange(YearHalf.from(date));
			case YEARLY -> new DateRange(Year.from(date));
		};
	}

}
