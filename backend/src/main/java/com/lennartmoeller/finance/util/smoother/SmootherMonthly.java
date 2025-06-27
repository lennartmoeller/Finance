package com.lennartmoeller.finance.util.smoother;

import com.lennartmoeller.finance.dto.StatsMetricDTO;
import com.lennartmoeller.finance.model.CategorySmoothType;
import com.lennartmoeller.finance.util.DateRange;
import com.lennartmoeller.finance.util.YearHalf;
import com.lennartmoeller.finance.util.YearQuarter;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public class SmootherMonthly extends Smoother<YearMonth> {

    @Override
    public void add(YearMonth yearMonth, CategorySmoothType smoothType, Long amount) {
        addRawToData(yearMonth, amount);
        DateRange dateRange = getDateRange(yearMonth, smoothType);
        long totalMonths = dateRange.getMonths();
        double monthlyPortion = (double) amount / totalMonths;
        dateRange.createMonthStream().forEach(ym -> addSmoothedToData(ym, monthlyPortion));
    }

    @Override
    public StatsMetricDTO get(YearMonth yearMonth) {
        List<StatsMetricDTO> statsMetricDTOs = this.data.entrySet().stream()
                .filter(entry -> YearMonth.from(entry.getKey()).equals(yearMonth))
                .map(Map.Entry::getValue)
                .toList();
        return StatsMetricDTO.add(statsMetricDTOs);
    }

    @Override
    protected DateRange getDateRange(YearMonth yearMonth, CategorySmoothType smoothType) {
        return switch (smoothType) {
            case DAILY, MONTHLY -> new DateRange(YearMonth.from(yearMonth));
            case QUARTER_YEARLY -> new DateRange(YearQuarter.from(yearMonth));
            case HALF_YEARLY -> new DateRange(YearHalf.from(yearMonth));
            case YEARLY -> new DateRange(Year.from(yearMonth));
        };
    }
}
