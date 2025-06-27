package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.dto.DailySavingStatsDTO;
import com.lennartmoeller.finance.dto.StatsMetricDTO;
import com.lennartmoeller.finance.model.TransactionType;
import com.lennartmoeller.finance.projection.DailyBalanceProjection;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import com.lennartmoeller.finance.util.DateRange;
import com.lennartmoeller.finance.util.smoother.SmootherDaily;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DailyBalanceStatsService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public List<DailySavingStatsDTO> getStats() {
        List<DailyBalanceProjection> dailyBalances = transactionRepository.getDailyBalances();

        Map<TransactionType, SmootherDaily> smoothers = Arrays.stream(TransactionType.values())
                .collect(Collectors.toMap(Function.identity(), transactionType -> new SmootherDaily()));
        dailyBalances.forEach(projection -> smoothers
                .get(projection.getCategory().getTransactionType())
                .add(projection.getDate(), projection.getCategory().getSmoothType(), projection.getBalance()));

        DateRange dateRange = new DateRange(dailyBalances.getFirst().getDate().withDayOfMonth(1), LocalDate.now());

        double initialBalance = accountRepository.getSummedStartBalance();

        AtomicReference<StatsMetricDTO> balance = new AtomicReference<>(new StatsMetricDTO());
        balance.get().setRaw(initialBalance);
        balance.get().setSmoothed(initialBalance);

        AtomicReference<StatsMetricDTO> target = new AtomicReference<>(new StatsMetricDTO());
        target.get().setRaw(initialBalance);
        target.get().setSmoothed(initialBalance);

        return dateRange
                .createDateStream()
                .map(date -> {
                    DailySavingStatsDTO dailySavingStatsDTO = new DailySavingStatsDTO();
                    dailySavingStatsDTO.setDate(date);

                    StatsMetricDTO incomeBalance =
                            smoothers.get(TransactionType.INCOME).get(date);
                    StatsMetricDTO investmentBalance =
                            smoothers.get(TransactionType.INVESTMENT).get(date);
                    StatsMetricDTO expenseBalance =
                            smoothers.get(TransactionType.EXPENSE).get(date);

                    balance.set(StatsMetricDTO.add(
                            List.of(balance.get(), incomeBalance, investmentBalance, expenseBalance)));
                    dailySavingStatsDTO.setBalance(balance.get());

                    target.set(StatsMetricDTO.add(
                            List.of(target.get(), StatsMetricDTO.multiply(incomeBalance, 0.2), investmentBalance)));
                    dailySavingStatsDTO.setTarget(target.get());

                    return dailySavingStatsDTO;
                })
                .toList();
    }

    public Map<YearMonth, StatsMetricDTO> getMonthlyMeanBalances() {
        return getStats().stream()
                .collect(Collectors.groupingBy(
                        dailyBalance -> YearMonth.from(dailyBalance.getDate()),
                        Collectors.collectingAndThen(
                                Collectors.mapping(DailySavingStatsDTO::getBalance, Collectors.toList()),
                                StatsMetricDTO::mean)));
    }
}
