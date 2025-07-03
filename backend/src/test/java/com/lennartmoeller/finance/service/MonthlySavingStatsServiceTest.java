package com.lennartmoeller.finance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.*;
import com.lennartmoeller.finance.model.InflationRate;
import com.lennartmoeller.finance.model.TransactionType;
import com.lennartmoeller.finance.projection.MonthlyDepositsProjection;
import com.lennartmoeller.finance.repository.InflationRateRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import com.lennartmoeller.finance.util.DateRange;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MonthlySavingStatsServiceTest {
    private DailyBalanceStatsService dailyBalanceStatsService;
    private MonthlyCategoryBalanceStatsService monthlyCategoryBalanceStatsService;
    private InflationRateRepository inflationRateRepository;
    private TransactionRepository transactionRepository;
    private MonthlySavingStatsService service;

    private static StatsMetricDTO metric(double raw) {
        StatsMetricDTO dto = new StatsMetricDTO();
        dto.setRaw(raw);
        dto.setSmoothed(raw);
        return dto;
    }

    private static CellStatsDTO cell(StatsMetricDTO m) {
        CellStatsDTO c = new CellStatsDTO();
        c.setSurplus(m);
        c.setTarget(0.0);
        PerformanceDTO perf = new PerformanceDTO();
        c.setPerformance(perf);
        return c;
    }

    @BeforeEach
    void setUp() {
        dailyBalanceStatsService = mock(DailyBalanceStatsService.class);
        monthlyCategoryBalanceStatsService = mock(MonthlyCategoryBalanceStatsService.class);
        inflationRateRepository = mock(InflationRateRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        service = new MonthlySavingStatsService(
                dailyBalanceStatsService,
                monthlyCategoryBalanceStatsService,
                inflationRateRepository,
                transactionRepository);
    }

    @Test
    void testGetStats() {
        YearMonth ym1 = YearMonth.of(2021, 1);
        YearMonth ym2 = YearMonth.of(2021, 2);

        Map<YearMonth, StatsMetricDTO> meanBalances = Map.of(
                ym1, metric(1000),
                ym2, metric(1100));
        when(dailyBalanceStatsService.getMonthlyMeanBalances()).thenReturn(meanBalances);

        DateRange range = new DateRange(ym1, ym2);
        MonthlyCategoryStatsDTO categoryStats = new MonthlyCategoryStatsDTO();
        categoryStats.setStartDate(range.getStartDate());
        categoryStats.setEndDate(range.getEndDate());

        CategoryStatsDTO incomeCategory = new CategoryStatsDTO();
        incomeCategory.setStats(new RowStatsDTO(Map.of(
                ym1, cell(metric(100)),
                ym2, cell(metric(120)))));
        TransactionTypeStatsDTO incomeStats = new TransactionTypeStatsDTO(List.of(incomeCategory), range);

        CategoryStatsDTO investCategory = new CategoryStatsDTO();
        investCategory.setStats(new RowStatsDTO(Map.of(
                ym1, cell(metric(20)),
                ym2, cell(metric(30)))));
        TransactionTypeStatsDTO investStats = new TransactionTypeStatsDTO(List.of(investCategory), range);

        CategoryStatsDTO expenseCategory = new CategoryStatsDTO();
        expenseCategory.setStats(new RowStatsDTO(Map.of(
                ym1, cell(metric(-50)),
                ym2, cell(metric(-80)))));
        TransactionTypeStatsDTO expenseStats = new TransactionTypeStatsDTO(List.of(expenseCategory), range);

        categoryStats.setStats(Map.of(
                TransactionType.INCOME, incomeStats,
                TransactionType.INVESTMENT, investStats,
                TransactionType.EXPENSE, expenseStats));
        when(monthlyCategoryBalanceStatsService.getStats()).thenReturn(categoryStats);

        InflationRate r1 = new InflationRate();
        r1.setYearMonth(ym1);
        r1.setRate(0.02); // 2%
        InflationRate r2 = new InflationRate();
        r2.setYearMonth(ym2);
        r2.setRate(0.03); // 3%
        when(inflationRateRepository.findAll()).thenReturn(List.of(r1, r2));

        MonthlyDepositsProjection p1 = new SimpleDeposit("2021-01", 500L);
        MonthlyDepositsProjection p2 = new SimpleDeposit("2021-02", 600L);
        when(transactionRepository.getMonthlyDeposits()).thenReturn(List.of(p1, p2));

        List<MonthlySavingStatsDTO> result = service.getStats();

        MonthlySavingStatsDTO dto1 = result.getFirst();
        assertEquals(ym1, dto1.getYearMonth());
        assertEquals(70.0, dto1.getBalanceChange().getRaw()); // income(100)+invest(20)+expense(-50)
        assertEquals(40.0, dto1.getBalanceChangeTarget().getRaw()); // income*0.2 + invest
        assertEquals(500L, dto1.getDeposits());
        assertEquals(20.0, dto1.getDepositsTarget());
        assertEquals(-20.0, dto1.getInflationLoss()); // 1000 * -0.02
        assertEquals(20.0, dto1.getInvestmentRevenue());

        MonthlySavingStatsDTO dto2 = result.get(1);
        assertEquals(ym2, dto2.getYearMonth());
        assertEquals(70.0, dto2.getBalanceChange().getRaw());
        assertEquals(54.0, dto2.getBalanceChangeTarget().getRaw());
        assertEquals(600L, dto2.getDeposits());
        assertEquals(24.0, dto2.getDepositsTarget());
        assertEquals(-33.0, dto2.getInflationLoss()); // 1100 * -0.03
        assertEquals(30.0, dto2.getInvestmentRevenue());
    }

    private static class SimpleDeposit implements MonthlyDepositsProjection {
        private final String yearMonth;
        private final Long deposits;

        SimpleDeposit(String yearMonth, Long deposits) {
            this.yearMonth = yearMonth;
            this.deposits = deposits;
        }

        @Override
        public String getYearMonth() {
            return yearMonth;
        }

        @Override
        public Long getDeposits() {
            return deposits;
        }
    }
}
