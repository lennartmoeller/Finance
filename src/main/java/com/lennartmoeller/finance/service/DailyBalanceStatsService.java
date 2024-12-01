package com.lennartmoeller.finance.service;

import com.google.common.util.concurrent.AtomicDouble;
import com.lennartmoeller.finance.dto.DailyBalanceStatsDTO;
import com.lennartmoeller.finance.dto.StatsMetricDTO;
import com.lennartmoeller.finance.model.CategorySmoothType;
import com.lennartmoeller.finance.projection.DailyBalanceProjection;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import com.lennartmoeller.finance.util.DateRange;
import com.lennartmoeller.finance.util.YearHalf;
import com.lennartmoeller.finance.util.YearQuarter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.lennartmoeller.finance.util.AggregationUtils.aggregateBalances;

@Service
@RequiredArgsConstructor
public class DailyBalanceStatsService {

	private final AccountRepository accountRepository;
	private final TransactionRepository transactionRepository;

	public List<DailyBalanceStatsDTO> getStats() {
		List<DailyBalanceProjection> dailyBalances = transactionRepository.getDailyBalances();

		DateRange dateRange = new DateRange(
			dailyBalances.getFirst().getDate().withDayOfMonth(1),
			LocalDate.now()
		);

		Map<CategorySmoothType, List<DailyBalanceProjection>> dailyBalancesBySmoothType = dailyBalances.stream()
			.collect(Collectors.groupingBy(projection -> projection.getCategory().getSmoothType()));

		Map<LocalDate, Long> rawBalancesMap = aggregateBalances(dailyBalances, LocalDate::from);
		Map<LocalDate, Long> smoothedBalancesMapDaily = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.DAILY), LocalDate::from);
		Map<YearMonth, Long> smoothedBalancesMapMonthly = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.MONTHLY), YearMonth::from);
		Map<YearQuarter, Long> smoothedBalancesMapQuarterYearly = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.QUARTER_YEARLY), YearQuarter::from);
		Map<YearHalf, Long> smoothedBalancesMapHalfYearly = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.HALF_YEARLY), YearHalf::from);
		Map<Year, Long> smoothedBalancesMapYearly = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.YEARLY), Year::from);

		double initialBalance = accountRepository.getSummedStartBalance();
		AtomicDouble balance = new AtomicDouble(initialBalance);
		AtomicDouble smoothedBalance = new AtomicDouble(initialBalance);

		return dateRange.createDateStream().map(date -> {
			DailyBalanceStatsDTO dailyBalanceStatsDTO = new DailyBalanceStatsDTO();
			dailyBalanceStatsDTO.setDate(date);

			StatsMetricDTO statsMetricDTO = new StatsMetricDTO();

			balance.addAndGet(Optional.ofNullable(rawBalancesMap.get(date)).orElse(0L));

			double surplusesOfDailySmoothedTransactions = smoothedBalancesMapDaily.getOrDefault(date, 0L).doubleValue();
			smoothedBalance.addAndGet(surplusesOfDailySmoothedTransactions);

			YearMonth month = YearMonth.from(date);
			double surplusesOfMonthlySmoothedTransactions = smoothedBalancesMapMonthly.getOrDefault(month, 0L).doubleValue();
			long daysInMonth = dateRange.getOverlapDays(new DateRange(month));
			smoothedBalance.addAndGet(surplusesOfMonthlySmoothedTransactions / daysInMonth);

			YearQuarter yearQuarter = YearQuarter.from(date);
			double surplusesOfQuarterYearlySmoothedTransactions = smoothedBalancesMapQuarterYearly.getOrDefault(yearQuarter, 0L).doubleValue();
			long daysInQuarterYear = dateRange.getOverlapDays(new DateRange(yearQuarter));
			smoothedBalance.addAndGet(surplusesOfQuarterYearlySmoothedTransactions / daysInQuarterYear);

			YearHalf yearHalf = YearHalf.from(date);
			double surplusesOfHalfYearlySmoothedTransactions = smoothedBalancesMapHalfYearly.getOrDefault(yearHalf, 0L).doubleValue();
			long daysInHalfYear = dateRange.getOverlapDays(new DateRange(yearHalf));
			smoothedBalance.addAndGet(surplusesOfHalfYearlySmoothedTransactions / daysInHalfYear);

			Year year = Year.from(date);
			double surplusesOfYearlySmoothedTransactions = smoothedBalancesMapYearly.getOrDefault(year, 0L).doubleValue();
			long daysInYear = dateRange.getOverlapDays(new DateRange(year));
			smoothedBalance.addAndGet(surplusesOfYearlySmoothedTransactions / daysInYear);

			statsMetricDTO.setRaw(balance.get());
			statsMetricDTO.setSmoothed(smoothedBalance.get());

			dailyBalanceStatsDTO.setBalance(statsMetricDTO);

			return dailyBalanceStatsDTO;
		}).toList();
	}

	public Map<YearMonth, StatsMetricDTO> getMonthlyMeanBalances() {
		return getStats().stream()
			.collect(Collectors.groupingBy(
				dailyBalance -> YearMonth.from(dailyBalance.getDate()),
				Collectors.collectingAndThen(
					Collectors.mapping(DailyBalanceStatsDTO::getBalance, Collectors.toList()),
					StatsMetricDTO::mean
				)
			));
	}

}
