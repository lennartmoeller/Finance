package com.lennartmoeller.finance.service;

import com.google.common.util.concurrent.AtomicDouble;
import com.lennartmoeller.finance.dto.*;
import com.lennartmoeller.finance.mapper.CategoryMapper;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.CategorySmoothType;
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

		StatsDTO statsDTO = new StatsDTO();

		statsDTO.setStartDate(dailyBalances.getFirst().getDate().withDayOfMonth(1));
		statsDTO.setEndDate(LocalDate.now());

		DateRange dateRange = new DateRange(statsDTO.getStartDate(), statsDTO.getEndDate());

		List<DailyStatsDTO> dailyStats = getDailyStats(dailyBalances, dateRange);
		statsDTO.setDailyStats(dailyStats);

		List<CategoryStatsNodeDTO> categoryStats = getCategoryStats(dailyBalances, dateRange);
		statsDTO.setCategoryStats(categoryStats);

		statsDTO.setIncomeStats(Collections.emptyList()); // TODO
		statsDTO.setExpenseStats(Collections.emptyList()); // TODO
		statsDTO.setSurplusStats(Collections.emptyList()); // TODO

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

	private List<CategoryStatsNodeDTO> getCategoryStats(List<DailyBalanceProjection> dailyBalances, DateRange dateRange) {
		List<Category> categories = categoryRepository.findAll();
		return getCategoryStats(dailyBalances, dateRange, categories, null);
	}

	private List<CategoryStatsNodeDTO> getCategoryStats(List<DailyBalanceProjection> dailyBalances, DateRange dateRange, List<Category> categories, Category parent) {
		return categories.stream()
			.filter(category -> category.getParent() == parent)
			.sorted(Comparator.comparing(Category::getLabel))
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
					List<MonthlyStatsDTO> monthlyStats = calculateMonthlyCategoryStats(category, categoriesDailyBalances, dateRange);
					categoryStatsNodeDTO.setStatistics(monthlyStats);
				} else {
					// non-leaf node, has no own transactions, so sum up children statistics
					Map<YearMonth, List<MonthlyStatsDTO>> categoryStatsMap = childStatisticNodes.stream()
						.flatMap(child -> child.getStatistics().stream())
						.collect(Collectors.groupingBy(MonthlyStatsDTO::getMonth));

					DescriptiveStatistics rawSurplusesDS = new DescriptiveStatistics();
					DescriptiveStatistics smoothedSurplusesDS = new DescriptiveStatistics();

					List<MonthlyStatsDTO> monthlyStats = dateRange.createMonthStream()
						.map(month -> {
							MonthlyStatsDTO monthlyStatsDTO = categoryStatsMap.get(month).stream()
								.reduce(MonthlyStatsDTO.empty(month), MonthlyStatsDTO::add);
							rawSurplusesDS.addValue(monthlyStatsDTO.getSurplus().getRaw());
							smoothedSurplusesDS.addValue(monthlyStatsDTO.getSurplus().getSmoothed());
							return monthlyStatsDTO;
						}).toList();

					monthlyStats.forEach(monthlyStatsDTO -> monthlyStatsDTO.calculatePerformance(rawSurplusesDS, smoothedSurplusesDS));

					categoryStatsNodeDTO.setStatistics(monthlyStats);
				}

				return categoryStatsNodeDTO;
			})
			.toList();
	}

	private List<MonthlyStatsDTO> calculateMonthlyCategoryStats(Category category, List<DailyBalanceProjection> dailyBalances, DateRange dateRange) {
		if (dailyBalances.isEmpty()) {
			return Collections.emptyList();
		}

		Map<CategorySmoothType, List<DailyBalanceProjection>> dailyBalancesBySmoothType = dailyBalances.stream()
			.collect(Collectors.groupingBy(projection -> projection.getCategory().getSmoothType()));
		List<DailyBalanceProjection> dailyBalancesWithDailyOrMonthlySmoothing = Stream.concat(
			dailyBalancesBySmoothType.getOrDefault(CategorySmoothType.DAILY, Collections.emptyList()).stream(),
			dailyBalancesBySmoothType.getOrDefault(CategorySmoothType.MONTHLY, Collections.emptyList()).stream()
		).toList();

		Map<YearMonth, Long> rawBalancesMap = aggregateBalances(dailyBalances, YearMonth::from);
		Map<YearMonth, Long> smoothedBalancesMapMonthly = aggregateBalances(dailyBalancesWithDailyOrMonthlySmoothing, YearMonth::from);
		Map<YearQuarter, Long> smoothedBalancesMapQuarterYearly = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.QUARTER_YEARLY), YearQuarter::from);
		Map<YearHalf, Long> smoothedBalancesMapHalfYearly = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.HALF_YEARLY), YearHalf::from);
		Map<Year, Long> smoothedBalancesMapYearly = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.YEARLY), Year::from);

		DescriptiveStatistics rawSurplusesDS = new DescriptiveStatistics();
		DescriptiveStatistics smoothedSurplusesDS = new DescriptiveStatistics();

		List<MonthlyStatsDTO> monthlyCategoryStats = dateRange.createMonthStream().map(month -> {
			// calculate raw surplus

			long rawSurplus = MapUtils.getObject(rawBalancesMap, month, 0L);

			// calculate smoothed surplus

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

			// calculate deviation

			StatsMetricDTO deviation = new StatsMetricDTO();
			deviation.setRaw(rawSurplus - target);
			deviation.setSmoothed(smoothedSurplus - target);

			// create MonthlyStatsDTO

			StatsMetricDTO surplus = new StatsMetricDTO();
			surplus.setRaw(rawSurplus);
			surplus.setSmoothed(smoothedSurplus);

			MonthlyStatsDTO monthlyStatsDTO = new MonthlyStatsDTO(month);
			monthlyStatsDTO.setSurplus(surplus);
			monthlyStatsDTO.setTarget(target);
			monthlyStatsDTO.setDeviation(deviation);

			// add surpluses to descriptive statistics

			rawSurplusesDS.addValue(surplus.getRaw());
			smoothedSurplusesDS.addValue(surplus.getSmoothed());

			return monthlyStatsDTO;
		}).toList();

		// calculate performance

		monthlyCategoryStats.forEach(monthlyStatsDTO -> monthlyStatsDTO.calculatePerformance(rawSurplusesDS, smoothedSurplusesDS));

		return monthlyCategoryStats;
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

}
