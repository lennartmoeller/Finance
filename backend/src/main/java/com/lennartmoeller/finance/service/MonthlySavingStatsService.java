package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.dto.MonthlyCategoryStatsDTO;
import com.lennartmoeller.finance.dto.MonthlySavingStatsDTO;
import com.lennartmoeller.finance.dto.StatsMetricDTO;
import com.lennartmoeller.finance.model.InflationRate;
import com.lennartmoeller.finance.model.TransactionType;
import com.lennartmoeller.finance.projection.MonthlyDepositsProjection;
import com.lennartmoeller.finance.repository.InflationRateRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import com.lennartmoeller.finance.util.DateRange;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MonthlySavingStatsService {
    private final DailyBalanceStatsService dailyBalanceStatsService;
    private final InflationRateRepository inflationRateRepository;
    private final MonthlyCategoryBalanceStatsService monthlyCategoryBalanceStatsService;
    private final TransactionRepository transactionRepository;

    public List<MonthlySavingStatsDTO> getStats() {
        Map<YearMonth, StatsMetricDTO> monthlyMeanBalances = dailyBalanceStatsService.getMonthlyMeanBalances();

        MonthlyCategoryStatsDTO monthlyCategoryBalanceStats = monthlyCategoryBalanceStatsService.getStats();

        Map<YearMonth, Double> inflationRates = inflationRateRepository.findAll().stream()
                .collect(Collectors.toMap(InflationRate::getYearMonth, InflationRate::getRate));

        Map<YearMonth, Long> monthlyDeposits = transactionRepository.getMonthlyDeposits().stream()
                .collect(Collectors.toMap(
                        projection -> YearMonth.parse(projection.getYearMonth()),
                        MonthlyDepositsProjection::getDeposits));

        DateRange dateRange =
                new DateRange(monthlyCategoryBalanceStats.getStartDate(), monthlyCategoryBalanceStats.getEndDate());

        return dateRange
                .createMonthStream()
                .map(yearMonth -> {
                    MonthlySavingStatsDTO dto = new MonthlySavingStatsDTO();

                    dto.setYearMonth(yearMonth);

                    StatsMetricDTO incomes = monthlyCategoryBalanceStats
                            .getStats()
                            .get(TransactionType.INCOME)
                            .getTotalStats()
                            .getMonthly()
                            .get(yearMonth)
                            .getSurplus();
                    StatsMetricDTO investments = monthlyCategoryBalanceStats
                            .getStats()
                            .get(TransactionType.INVESTMENT)
                            .getTotalStats()
                            .getMonthly()
                            .get(yearMonth)
                            .getSurplus();

                    StatsMetricDTO balanceChange = monthlyCategoryBalanceStats
                            .getTotalStats()
                            .getMonthly()
                            .get(yearMonth)
                            .getSurplus();
                    dto.setBalanceChange(balanceChange);

                    StatsMetricDTO balanceChangeTarget = new StatsMetricDTO();
                    balanceChangeTarget.setRaw(incomes.getRaw() * 0.2 + investments.getRaw());
                    balanceChangeTarget.setSmoothed(incomes.getSmoothed() * 0.2 + investments.getSmoothed());
                    dto.setBalanceChangeTarget(balanceChangeTarget);

                    Long deposits = monthlyDeposits.getOrDefault(yearMonth, 0L);
                    dto.setDeposits(deposits);

                    dto.setDepositsTarget(incomes.getRaw() * 0.2);

                    Double rate = inflationRates.getOrDefault(yearMonth, 0.0);
                    double meanBalance = monthlyMeanBalances.get(yearMonth).getRaw();
                    double inflation = meanBalance * -rate;
                    dto.setInflationLoss(inflation);

                    dto.setInvestmentRevenue(investments.getRaw());

                    return dto;
                })
                .toList();
    }
}
