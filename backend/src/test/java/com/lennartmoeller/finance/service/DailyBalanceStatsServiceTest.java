package com.lennartmoeller.finance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.lennartmoeller.finance.dto.DailySavingStatsDTO;
import com.lennartmoeller.finance.dto.StatsMetricDTO;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.CategorySmoothType;
import com.lennartmoeller.finance.model.TransactionType;
import com.lennartmoeller.finance.projection.DailyBalanceProjection;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import com.lennartmoeller.finance.util.DateRange;
import com.lennartmoeller.finance.util.smoother.SmootherDaily;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DailyBalanceStatsServiceTest {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private DailyBalanceStatsService service;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        service = new DailyBalanceStatsService(accountRepository, transactionRepository);
    }

    @Test
    void testGetStats() {
        LocalDate now = LocalDate.now();
        LocalDate first = now.withDayOfMonth(1);

        Category incomeCat = new Category();
        incomeCat.setTransactionType(TransactionType.INCOME);
        incomeCat.setSmoothType(CategorySmoothType.DAILY);
        Category expenseCat = new Category();
        expenseCat.setTransactionType(TransactionType.EXPENSE);
        expenseCat.setSmoothType(CategorySmoothType.DAILY);
        Category investCat = new Category();
        investCat.setTransactionType(TransactionType.INVESTMENT);
        investCat.setSmoothType(CategorySmoothType.DAILY);

        List<DailyBalanceProjection> projections = List.of(
                new SimpleProjection(first, incomeCat, 50L),
                new SimpleProjection(first, expenseCat, -20L),
                new SimpleProjection(first.plusDays(1), investCat, 30L));
        when(transactionRepository.getDailyBalances()).thenReturn(projections);
        when(accountRepository.getSummedStartBalance()).thenReturn(1000L);

        List<DailySavingStatsDTO> result = service.getStats();

        // compute expected using same algorithm
        Map<TransactionType, SmootherDaily> smoothers = Map.of(
                TransactionType.INCOME, new SmootherDaily(),
                TransactionType.EXPENSE, new SmootherDaily(),
                TransactionType.INVESTMENT, new SmootherDaily());
        projections.forEach(p -> smoothers
                .get(p.getCategory().getTransactionType())
                .add(p.getDate(), p.getCategory().getSmoothType(), p.getBalance()));

        DateRange range = new DateRange(first, now);
        List<DailySavingStatsDTO> expected = new ArrayList<>();
        StatsMetricDTO balance = new StatsMetricDTO();
        balance.setRaw(1000);
        balance.setSmoothed(1000);
        StatsMetricDTO target = new StatsMetricDTO();
        target.setRaw(1000);
        target.setSmoothed(1000);
        for (LocalDate d : range.createDateStream().toList()) {
            StatsMetricDTO inc = smoothers.get(TransactionType.INCOME).get(d);
            StatsMetricDTO exp = smoothers.get(TransactionType.EXPENSE).get(d);
            StatsMetricDTO inv = smoothers.get(TransactionType.INVESTMENT).get(d);
            balance = StatsMetricDTO.add(List.of(balance, inc, inv, exp));
            target = StatsMetricDTO.add(List.of(target, StatsMetricDTO.multiply(inc, 0.2), inv));
            DailySavingStatsDTO dto = new DailySavingStatsDTO();
            dto.setDate(d);
            dto.setBalance(balance);
            dto.setTarget(target);
            expected.add(dto);
        }

        assertEquals(expected.size(), result.size());
        for (int i = 0; i < expected.size(); i++) {
            DailySavingStatsDTO expDto = expected.get(i);
            DailySavingStatsDTO actDto = result.get(i);
            assertEquals(expDto.getDate(), actDto.getDate());
            assertEquals(expDto.getBalance().getRaw(), actDto.getBalance().getRaw(), 0.0001);
            assertEquals(expDto.getTarget().getRaw(), actDto.getTarget().getRaw(), 0.0001);
        }
    }

    @Test
    void testGetMonthlyMeanBalances() {
        DailyBalanceStatsService spy = spy(new DailyBalanceStatsService(accountRepository, transactionRepository));
        DailySavingStatsDTO d1 = new DailySavingStatsDTO();
        d1.setDate(LocalDate.of(2021, 1, 1));
        StatsMetricDTO s1 = new StatsMetricDTO();
        s1.setRaw(10);
        s1.setSmoothed(20);
        d1.setBalance(s1);
        DailySavingStatsDTO d2 = new DailySavingStatsDTO();
        d2.setDate(LocalDate.of(2021, 1, 2));
        StatsMetricDTO s2 = new StatsMetricDTO();
        s2.setRaw(30);
        s2.setSmoothed(40);
        d2.setBalance(s2);
        doReturn(List.of(d1, d2)).when(spy).getStats();

        Map<YearMonth, StatsMetricDTO> result = spy.getMonthlyMeanBalances();

        StatsMetricDTO mean = result.get(YearMonth.of(2021, 1));
        assertEquals(20.0, mean.getRaw());
        assertEquals(30.0, mean.getSmoothed());
    }

    private static class SimpleProjection implements DailyBalanceProjection {
        private final LocalDate date;
        private final Category category;
        private final Long balance;

        SimpleProjection(LocalDate date, Category category, Long balance) {
            this.date = date;
            this.category = category;
            this.balance = balance;
        }

        @Override
        public LocalDate getDate() {
            return date;
        }

        @Override
        public Category getCategory() {
            return category;
        }

        @Override
        public Long getBalance() {
            return balance;
        }
    }
}
