package com.lennartmoeller.finance.service;

import com.google.common.util.concurrent.AtomicDouble;
import com.lennartmoeller.finance.dto.*;
import com.lennartmoeller.finance.mapper.CategoryMapper;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.CategorySmoothType;
import com.lennartmoeller.finance.model.TransactionType;
import com.lennartmoeller.finance.projection.DailyBalanceProjection;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.CategoryRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import com.lennartmoeller.finance.util.DateRange;
import com.lennartmoeller.finance.util.YearHalf;
import com.lennartmoeller.finance.util.YearQuarter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Log
public class StatisticsService {

	private final AccountRepository accountRepository;
	private final CategoryRepository categoryRepository;
	private final TransactionRepository transactionRepository;
	private final CategoryMapper categoryMapper;

	public StatsDTO getStatistics() {
		StatsDTO statsDTO = new StatsDTO();

		List<DailyBalanceProjection> dailyBalances = transactionRepository.getDailyBalances();
		if (dailyBalances.isEmpty()) {
			return statsDTO;
		}

		statsDTO.setStartDate(dailyBalances.getFirst().getDate().withDayOfMonth(1));
		statsDTO.setEndDate(LocalDate.now());

		DateRange dateRange = new DateRange(statsDTO.getStartDate(), statsDTO.getEndDate());

		List<DailyStatsDTO> dailyStatistics = getDailyStatistics(dailyBalances, dateRange);
		statsDTO.setDailyStats(dailyStatistics);

		List<CategoryStatsNodeDTO> categoryStatistics = getCategoryStatistics(dailyBalances, dateRange);
		statsDTO.setCategoryStats(categoryStatistics);

		List<MonthlyStatsDTO> monthlyStatistics = getMonthlyStatistics(categoryStatistics, dateRange);
		statsDTO.setMonthlyStats(monthlyStatistics);

		return statsDTO;
	}

	private List<DailyStatsDTO> getDailyStatistics(List<DailyBalanceProjection> dailyBalances, DateRange dateRange) {
		Map<CategorySmoothType, List<DailyBalanceProjection>> dailyBalancesBySmoothType = dailyBalances.stream()
			.collect(Collectors.groupingBy(projection -> projection.getCategory().getSmoothType()));

		Map<LocalDate, Long> rawBalancesMap = aggregateBalances(dailyBalances, LocalDate::from);
		Map<LocalDate, Long> smoothedBalancesMapDaily = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.DAILY), LocalDate::from);
		Map<YearMonth, Long> smoothedBalancesMapMonthly = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.MONTHLY), YearMonth::from);
		Map<YearQuarter, Long> smoothedBalancesMapQuarterYearly = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.QUARTER_YEARLY), YearQuarter::from);
		Map<YearHalf, Long> smoothedBalancesMapHalfYearly = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.HALF_YEARLY), YearHalf::from);
		Map<Year, Long> smoothedBalancesMapYearly = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.YEARLY), Year::from);

		long initialBalance = accountRepository.getSummedStartBalance();
		AtomicLong balance = new AtomicLong(initialBalance);
		AtomicDouble smoothedBalance = new AtomicDouble(initialBalance);

		return dateRange.createDateStream().map(date -> {
			DailyStatsDTO dailyStatsDTO = new DailyStatsDTO();
			dailyStatsDTO.setDate(date);

			long surplus = Optional.ofNullable(rawBalancesMap.get(date)).orElse(0L);
			dailyStatsDTO.setBalance(balance.addAndGet(surplus));

			double smoothedSurplus = 0.0;

			double surplusesOfDailySmoothedTransactions = smoothedBalancesMapDaily.getOrDefault(date, 0L).doubleValue();
			smoothedSurplus += surplusesOfDailySmoothedTransactions;

			YearMonth month = YearMonth.from(date);
			double surplusesOfMonthlySmoothedTransactions = smoothedBalancesMapMonthly.getOrDefault(month, 0L).doubleValue();
			long daysInMonth = dateRange.getOverlapDays(new DateRange(month));
			smoothedSurplus += surplusesOfMonthlySmoothedTransactions / daysInMonth;

			YearQuarter yearQuarter = YearQuarter.from(date);
			double surplusesOfQuarterYearlySmoothedTransactions = smoothedBalancesMapQuarterYearly.getOrDefault(yearQuarter, 0L).doubleValue();
			long daysInQuarterYear = dateRange.getOverlapDays(new DateRange(yearQuarter));
			smoothedSurplus += surplusesOfQuarterYearlySmoothedTransactions / daysInQuarterYear;

			YearHalf yearHalf = YearHalf.from(date);
			double surplusesOfHalfYearlySmoothedTransactions = smoothedBalancesMapHalfYearly.getOrDefault(yearHalf, 0L).doubleValue();
			long daysInHalfYear = dateRange.getOverlapDays(new DateRange(yearHalf));
			smoothedSurplus += surplusesOfHalfYearlySmoothedTransactions / daysInHalfYear;

			Year year = Year.from(date);
			double surplusesOfYearlySmoothedTransactions = smoothedBalancesMapYearly.getOrDefault(year, 0L).doubleValue();
			long daysInYear = dateRange.getOverlapDays(new DateRange(year));
			smoothedSurplus += surplusesOfYearlySmoothedTransactions / daysInYear;

			dailyStatsDTO.setSmoothedBalance(smoothedBalance.addAndGet(smoothedSurplus));

			return dailyStatsDTO;
		}).toList();
	}

	private List<CategoryStatsNodeDTO> getCategoryStatistics(List<DailyBalanceProjection> dailyBalances, DateRange dateRange) {
		List<Category> categories = categoryRepository.findAll();
		return getCategoryStatistics(dailyBalances, dateRange, categories, null);
	}

	private List<CategoryStatsNodeDTO> getCategoryStatistics(List<DailyBalanceProjection> dailyBalances, DateRange dateRange, List<Category> categories, Category parent) {
		return categories.stream()
			.filter(category -> category.getParent() == parent)
			.sorted(Comparator.comparing(Category::getLabel))
			.map(category -> {
				CategoryStatsNodeDTO categoryStatsNodeDTO = new CategoryStatsNodeDTO();
				categoryStatsNodeDTO.setCategory(categoryMapper.toDto(category));

				// calculate statistic nodes for children
				List<CategoryStatsNodeDTO> childStatisticNodes = getCategoryStatistics(dailyBalances, dateRange, categories, category);
				categoryStatsNodeDTO.setChildren(childStatisticNodes);

				// calculate statistics for category
				if (childStatisticNodes.isEmpty()) {
					// leaf node, has own transactions and no children, so calculate own statistics
					List<DailyBalanceProjection> categoriesDailyBalances = dailyBalances.stream()
						.filter(projection -> projection.getCategory().equals(category))
						.toList();
					List<MonthlyStatsDTO> statistics = calculateMonthlyStats(categoriesDailyBalances, dateRange);
					categoryStatsNodeDTO.setStatistics(statistics);
				} else {
					// non-leaf node, has no own transactions, so sum up children statistics
					Map<YearMonth, List<MonthlyStatsDTO>> childStatisticNodesMap = childStatisticNodes.stream()
						.flatMap(child -> child.getStatistics().stream())
						.collect(Collectors.groupingBy(MonthlyStatsDTO::getMonth));
					List<MonthlyStatsDTO> statistics = dateRange.createMonthStream().map(month ->
						childStatisticNodesMap.get(month).stream()
							.reduce(MonthlyStatsDTO.empty(month), MonthlyStatsDTO::add)).toList();
					categoryStatsNodeDTO.setStatistics(statistics);
				}

				return categoryStatsNodeDTO;
			})
			.toList();
	}

	private List<MonthlyStatsDTO> getMonthlyStatistics(List<CategoryStatsNodeDTO> categoryStatistics, DateRange dateRange) {
		Map<YearMonth, List<MonthlyStatsDTO>> categoryStatisticsMap = categoryStatistics.stream()
			.flatMap(x -> x.getStatistics().stream())
			.collect(Collectors.groupingBy(MonthlyStatsDTO::getMonth));

		return dateRange.createMonthStream().map(month ->
			categoryStatisticsMap.get(month).stream()
				.reduce(MonthlyStatsDTO.empty(month), MonthlyStatsDTO::add)
		).toList();
	}

	private List<MonthlyStatsDTO> calculateMonthlyStats(List<DailyBalanceProjection> dailyBalances, DateRange dateRange) {
		if (dailyBalances.isEmpty()) {
			return Collections.emptyList();
		}

		Map<CategorySmoothType, List<DailyBalanceProjection>> dailyBalancesBySmoothType = dailyBalances.stream()
			.collect(Collectors.groupingBy(projection -> projection.getCategory().getSmoothType()));
		List<DailyBalanceProjection> dailyBalancesWithDailyOrMonthlySmoothing = Stream.concat(
			dailyBalancesBySmoothType.getOrDefault(CategorySmoothType.DAILY, Collections.emptyList()).stream(),
			dailyBalancesBySmoothType.getOrDefault(CategorySmoothType.MONTHLY, Collections.emptyList()).stream()
		).toList();

		Map<TransactionType, Map<YearMonth, Long>> rawBalancesMap = aggregateBalancesByTransactionType(dailyBalances, YearMonth::from);
		Map<TransactionType, Map<YearMonth, Long>> smoothedBalancesMapMonthly = aggregateBalancesByTransactionType(dailyBalancesWithDailyOrMonthlySmoothing, YearMonth::from);
		Map<TransactionType, Map<YearQuarter, Long>> smoothedBalancesMapQuarterYearly = aggregateBalancesByTransactionType(dailyBalancesBySmoothType.get(CategorySmoothType.QUARTER_YEARLY), YearQuarter::from);
		Map<TransactionType, Map<YearHalf, Long>> smoothedBalancesMapHalfYearly = aggregateBalancesByTransactionType(dailyBalancesBySmoothType.get(CategorySmoothType.HALF_YEARLY), YearHalf::from);
		Map<TransactionType, Map<Year, Long>> smoothedBalancesMapYearly = aggregateBalancesByTransactionType(dailyBalancesBySmoothType.get(CategorySmoothType.YEARLY), Year::from);

		return dateRange.createMonthStream().map(month -> {

			// initialize objects

			MonthlyStatsDTO monthlyStatsDTO = new MonthlyStatsDTO(month);

			StatsMetricDTO incomes = new StatsMetricDTO();
			StatsMetricDTO expenses = new StatsMetricDTO();

			// calculate raw values

			long rawIncomes = getTwoTimesOrDefault(rawBalancesMap, TransactionType.INCOME, month, 0L);
			incomes.setRaw(rawIncomes);

			long rawExpenses = getTwoTimesOrDefault(rawBalancesMap, TransactionType.EXPENSE, month, 0L);
			expenses.setRaw(rawExpenses);

			// calculate smoothed values

			double smoothedIncomes = 0.0;
			double smoothedExpenses = 0.0;

			smoothedIncomes += getTwoTimesOrDefault(smoothedBalancesMapMonthly, TransactionType.INCOME, month, 0L).doubleValue();
			smoothedExpenses += getTwoTimesOrDefault(smoothedBalancesMapMonthly, TransactionType.EXPENSE, month, 0L).doubleValue();

			YearQuarter yearQuarter = YearQuarter.from(month);
			long daysInQuarterYear = dateRange.getOverlapMonths(new DateRange(yearQuarter));
			smoothedIncomes += getTwoTimesOrDefault(smoothedBalancesMapQuarterYearly, TransactionType.INCOME, yearQuarter, 0L).doubleValue() / daysInQuarterYear;
			smoothedExpenses += getTwoTimesOrDefault(smoothedBalancesMapQuarterYearly, TransactionType.EXPENSE, yearQuarter, 0L).doubleValue() / daysInQuarterYear;

			YearHalf yearHalf = YearHalf.from(month);
			long daysInHalfYear = dateRange.getOverlapMonths(new DateRange(yearHalf));
			smoothedIncomes += getTwoTimesOrDefault(smoothedBalancesMapHalfYearly, TransactionType.INCOME, yearHalf, 0L).doubleValue() / daysInHalfYear;
			smoothedExpenses += getTwoTimesOrDefault(smoothedBalancesMapHalfYearly, TransactionType.EXPENSE, yearHalf, 0L).doubleValue() / daysInHalfYear;

			Year year = Year.from(month);
			long daysInYear = dateRange.getOverlapMonths(new DateRange(year));
			smoothedIncomes += getTwoTimesOrDefault(smoothedBalancesMapYearly, TransactionType.INCOME, year, 0L).doubleValue() / daysInYear;
			smoothedExpenses += getTwoTimesOrDefault(smoothedBalancesMapYearly, TransactionType.EXPENSE, year, 0L).doubleValue() / daysInYear;

			incomes.setSmoothed(smoothedIncomes);
			expenses.setSmoothed(smoothedExpenses);

			// set everything into main object

			monthlyStatsDTO.setIncomes(incomes);
			monthlyStatsDTO.setExpenses(expenses);
			monthlyStatsDTO.setSurplus(StatsMetricDTO.add(incomes, expenses));

			monthlyStatsDTO.setTarget(0.0); // TODO: Set target
			monthlyStatsDTO.setDeviation(StatsMetricDTO.empty()); // TODO: Set deviation

			return monthlyStatsDTO;
		}).toList();
	}

	private <K1, K2, T> T getTwoTimesOrDefault(Map<K1, Map<K2, T>> map, K1 key1, K2 key2, T defaultValue) {
		return MapUtils.getObject(MapUtils.getObject(map, key1, null), key2, defaultValue);
	}

	private <T> Map<T, Long> aggregateBalances(@Nullable List<DailyBalanceProjection> dailyBalances, Function<LocalDate, T> dateMapper) {
		if (dailyBalances == null) {
			return Collections.emptyMap();
		}
		return dailyBalances.stream()
			.collect(Collectors.toMap(
				projection -> dateMapper.apply(projection.getDate()),
				DailyBalanceProjection::getBalance,
				Long::sum
			));
	}

	private <T> Map<TransactionType, Map<T, Long>> aggregateBalancesByTransactionType(@Nullable List<DailyBalanceProjection> dailyBalances, Function<LocalDate, T> dateMapper) {
		if (dailyBalances == null) {
			return Collections.emptyMap();
		}
		return dailyBalances.stream()
			.collect(Collectors.groupingBy(
				projection -> projection.getCategory().getTransactionType(),
				Collectors.toMap(
					projection -> dateMapper.apply(projection.getDate()),
					DailyBalanceProjection::getBalance,
					Long::sum
				)
			));
	}

}
