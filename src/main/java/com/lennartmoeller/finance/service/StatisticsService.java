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
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StatisticsService {

	private final AccountRepository accountRepository;
	private final CategoryRepository categoryRepository;
	private final TransactionRepository transactionRepository;
	private final CategoryMapper categoryMapper;

	public StatsDTO getStatistics() {
		List<DailyBalanceProjection> dailyBalances = transactionRepository.getDailyBalances();
		if (dailyBalances.isEmpty()) {
			return StatsDTO.empty();
		}

		DateRange dateRange = new DateRange(
			dailyBalances.getFirst().getDate().withDayOfMonth(1),
			LocalDate.now()
		);

		StatsDTO statsDTO = new StatsDTO();

		List<DailyStatsDTO> dailyStats = getDailyStats(dailyBalances, dateRange);
		statsDTO.setDailyStats(dailyStats);

		Map<TransactionType, CategoryStatsDTO> categoryStatsByType = getCategoryStatsByType(dailyBalances, dateRange);
		statsDTO.setIncomeStats(categoryStatsByType.get(TransactionType.INCOME));
		statsDTO.setExpenseStats(categoryStatsByType.get(TransactionType.EXPENSE));

		statsDTO.setStartDate(dateRange.getStartDate());
		statsDTO.setEndDate(dateRange.getEndDate());

		return statsDTO;
	}

	private List<DailyStatsDTO> getDailyStats(List<DailyBalanceProjection> dailyBalances, DateRange dateRange) {
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
			DailyStatsDTO dailyStatsDTO = new DailyStatsDTO();
			dailyStatsDTO.setDate(date);

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

			dailyStatsDTO.setBalance(statsMetricDTO);

			return dailyStatsDTO;
		}).toList();
	}

	private Map<TransactionType, CategoryStatsDTO> getCategoryStatsByType(List<DailyBalanceProjection> dailyBalances, DateRange dateRange) {
		return categoryRepository.findAll().stream()
			.collect(Collectors.groupingBy(
				Category::getTransactionType,
				Collectors.collectingAndThen(
					Collectors.toList(),
					filteredCategories -> new CategoryStatsDTO(getCategoryStats(dailyBalances, dateRange, filteredCategories, null))
				)
			));
	}

	private List<CategoryStatsNodeDTO> getCategoryStats(List<DailyBalanceProjection> dailyBalances, DateRange dateRange, List<Category> categories, Category parent) {
		return categories.stream()
			.filter(category -> category.getParent() == parent)
			.map(category -> {
				CategoryStatsNodeDTO categoryStatsNodeDTO = new CategoryStatsNodeDTO();
				categoryStatsNodeDTO.setCategory(categoryMapper.toDto(category));

				// calculate statistic nodes for children
				List<CategoryStatsNodeDTO> childStatisticNodes = getCategoryStats(dailyBalances, dateRange, categories, category);
				categoryStatsNodeDTO.setChildren(childStatisticNodes);

				// calculate statistics for category
				if (childStatisticNodes.isEmpty()) {
					// leaf node, has own transactions and no children, so calculate own statistics
					List<DailyBalanceProjection> categoriesDailyBalances = dailyBalances.stream()
						.filter(projection -> projection.getCategory().equals(category))
						.toList();
					Map<YearMonth, CellStatsDTO> monthlyStats = calculateMonthlyCategoryStats(category, categoriesDailyBalances, dateRange);
					categoryStatsNodeDTO.setStats(new RowStatsDTO(monthlyStats));
				} else {
					// non-leaf node, has no own transactions, so sum up children statistics
					Map<YearMonth, List<CellStatsDTO>> categoryStatsMap = childStatisticNodes.stream()
						.flatMap(child -> child.getStats().getMonthly().entrySet().stream())
						.collect(Collectors.groupingBy(
							Map.Entry::getKey,
							Collectors.mapping(Map.Entry::getValue, Collectors.toList())
						));

					DescriptiveStatistics rawSurplusesDS = new DescriptiveStatistics();
					DescriptiveStatistics smoothedSurplusesDS = new DescriptiveStatistics();

					Map<YearMonth, CellStatsDTO> monthlyStats = dateRange.createMonthStream()
						.collect(Collectors.toMap(
							month -> month,
							month -> {
								CellStatsDTO cellStatsDTO = categoryStatsMap.get(month).stream()
									.reduce(CellStatsDTO.empty(), CellStatsDTO::add);
								rawSurplusesDS.addValue(cellStatsDTO.getSurplus().getRaw());
								smoothedSurplusesDS.addValue(cellStatsDTO.getSurplus().getSmoothed());
								return cellStatsDTO;
							}
						));

					monthlyStats.forEach((month, cellStatsDTO) -> cellStatsDTO.calculatePerformance(rawSurplusesDS, smoothedSurplusesDS));

					categoryStatsNodeDTO.setStats(new RowStatsDTO(monthlyStats));
				}

				return categoryStatsNodeDTO;
			})
			.sorted(Comparator.comparing(v -> -Math.abs(v.getStats().getMean().getSurplus().getSmoothed())))
			.toList();
	}

	private Map<YearMonth, CellStatsDTO> calculateMonthlyCategoryStats(Category category, List<DailyBalanceProjection> dailyBalances, DateRange dateRange) {
		if (dailyBalances.isEmpty()) {
			return Map.of();
		}

		Map<CategorySmoothType, List<DailyBalanceProjection>> dailyBalancesBySmoothType = dailyBalances.stream()
			.collect(Collectors.groupingBy(projection -> projection.getCategory().getSmoothType()));
		List<DailyBalanceProjection> dailyBalancesWithDailyOrMonthlySmoothing = Stream.concat(
			dailyBalancesBySmoothType.getOrDefault(CategorySmoothType.DAILY, List.of()).stream(),
			dailyBalancesBySmoothType.getOrDefault(CategorySmoothType.MONTHLY, List.of()).stream()
		).toList();

		Map<YearMonth, Long> rawBalancesMap = aggregateBalances(dailyBalances, YearMonth::from);
		Map<YearMonth, Long> smoothedBalancesMapMonthly = aggregateBalances(dailyBalancesWithDailyOrMonthlySmoothing, YearMonth::from);
		Map<YearQuarter, Long> smoothedBalancesMapQuarterYearly = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.QUARTER_YEARLY), YearQuarter::from);
		Map<YearHalf, Long> smoothedBalancesMapHalfYearly = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.HALF_YEARLY), YearHalf::from);
		Map<Year, Long> smoothedBalancesMapYearly = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.YEARLY), Year::from);

		DescriptiveStatistics rawSurplusesDS = new DescriptiveStatistics();
		DescriptiveStatistics smoothedSurplusesDS = new DescriptiveStatistics();

		Map<YearMonth, CellStatsDTO> monthlyCategoryStats = new HashMap<>();

		dateRange.createMonthStream().forEach(month -> {
			CellStatsDTO cellStats = new CellStatsDTO();

			StatsMetricDTO surplus = new StatsMetricDTO();

			// calculate surplus

			long rawSurplus = MapUtils.getObject(rawBalancesMap, month, 0L);

			surplus.setRaw(rawSurplus);

			double smoothedSurplus = 0.0;

			smoothedSurplus += MapUtils.getObject(smoothedBalancesMapMonthly, month, 0L).doubleValue();

			YearQuarter yearQuarter = YearQuarter.from(month);
			long daysInQuarterYear = dateRange.getOverlapMonths(new DateRange(yearQuarter));
			smoothedSurplus += MapUtils.getObject(smoothedBalancesMapQuarterYearly, yearQuarter, 0L).doubleValue() / daysInQuarterYear;

			YearHalf yearHalf = YearHalf.from(month);
			long daysInHalfYear = dateRange.getOverlapMonths(new DateRange(yearHalf));
			smoothedSurplus += MapUtils.getObject(smoothedBalancesMapHalfYearly, yearHalf, 0L).doubleValue() / daysInHalfYear;

			Year year = Year.from(month);
			long daysInYear = dateRange.getOverlapMonths(new DateRange(year));
			smoothedSurplus += MapUtils.getObject(smoothedBalancesMapYearly, year, 0L).doubleValue() / daysInYear;

			smoothedSurplus += Math.random() * 100; // TODO: Testing purposes only, remove this line

			surplus.setSmoothed(smoothedSurplus);

			cellStats.setSurplus(surplus);

			// calculate target

			DateRange monthRange = DateRange.getOverlapRange(dateRange, new DateRange(month));
			long daysInMonth = monthRange.getDays();
			double target = category.getTargets().stream()
				.mapToDouble(t -> {
					double amount = t.getAmount().doubleValue();
					long overlapDays = monthRange.getOverlapDays(new DateRange(t.getStart(), t.getEnd()));
					return amount / daysInMonth * overlapDays;
				})
				.sum();

			cellStats.setTarget(target);

			// add surpluses to descriptive statistics

			rawSurplusesDS.addValue(surplus.getRaw());
			smoothedSurplusesDS.addValue(surplus.getSmoothed());

			monthlyCategoryStats.put(month, cellStats);
		});

		// calculate performance
		monthlyCategoryStats.forEach((month, cellStats) -> cellStats.calculatePerformance(rawSurplusesDS, smoothedSurplusesDS));

		return monthlyCategoryStats;
	}

	private <T> Map<T, Long> aggregateBalances(@Nullable List<DailyBalanceProjection> dailyBalances, Function<LocalDate, T> dateMapper) {
		if (dailyBalances == null) {
			return Map.of();
		}
		return dailyBalances.stream()
			.collect(Collectors.toMap(
				projection -> dateMapper.apply(projection.getDate()),
				DailyBalanceProjection::getBalance,
				Long::sum
			));
	}

}
