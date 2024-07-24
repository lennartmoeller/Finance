package com.lennartmoeller.finance.service;

import com.google.common.util.concurrent.AtomicDouble;
import com.lennartmoeller.finance.dto.CategoryStatsNodeDTO;
import com.lennartmoeller.finance.dto.DailyStatsDTO;
import com.lennartmoeller.finance.dto.MonthlyStatsDTO;
import com.lennartmoeller.finance.dto.StatsDTO;
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
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

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
		Map<LocalDate, Long> dailyBalancesMap = dailyBalances.stream()
			.collect(Collectors.toMap(
				DailyBalanceProjection::getDate,
				DailyBalanceProjection::getBalance,
				Long::sum
			));

		Map<CategorySmoothType, List<DailyBalanceProjection>> dailyBalancesBySmoothType = dailyBalances.stream()
			.collect(Collectors.groupingBy(projection -> projection.getCategory().getSmoothType()));

		Map<LocalDate, Long> dailySmoothedTransactionsBalancesMap = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.DAILY), LocalDate::from);
		Map<YearMonth, Long> monthlySmoothedTransactionsBalancesMap = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.MONTHLY), YearMonth::from);
		Map<YearQuarter, Long> quarterYearlySmoothedTransactionsBalancesMap = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.QUARTER_YEARLY), YearQuarter::from);
		Map<YearHalf, Long> halfYearlySmoothedTransactionsBalancesMap = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.HALF_YEARLY), YearHalf::from);
		Map<Year, Long> yearlySmoothedTransactionsBalancesMap = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.YEARLY), Year::from);

		long initialBalance = accountRepository.getSummedStartBalance();
		AtomicLong balance = new AtomicLong(initialBalance);
		AtomicDouble smoothedBalance = new AtomicDouble(initialBalance);

		return dateRange.createDateStream().map(date -> {
			DailyStatsDTO dailyStatsDTO = new DailyStatsDTO();
			dailyStatsDTO.setDate(date);

			long surplus = Optional.ofNullable(dailyBalancesMap.get(date)).orElse(0L);
			dailyStatsDTO.setBalance(balance.addAndGet(surplus));

			double smoothedSurplus = 0.0;

			double surplusesOfDailySmoothedTransactions = dailySmoothedTransactionsBalancesMap.getOrDefault(date, 0L).doubleValue();
			smoothedSurplus += surplusesOfDailySmoothedTransactions;

			YearMonth month = YearMonth.from(date);
			double surplusesOfMonthlySmoothedTransactions = monthlySmoothedTransactionsBalancesMap.getOrDefault(month, 0L).doubleValue();
			long daysInMonth = dateRange.getOverlapDays(new DateRange(month));
			smoothedSurplus += surplusesOfMonthlySmoothedTransactions / daysInMonth;

			YearQuarter yearQuarter = YearQuarter.from(date);
			double surplusesOfQuarterYearlySmoothedTransactions = quarterYearlySmoothedTransactionsBalancesMap.getOrDefault(yearQuarter, 0L).doubleValue();
			long daysInQuarterYear = dateRange.getOverlapDays(new DateRange(yearQuarter));
			smoothedSurplus += surplusesOfQuarterYearlySmoothedTransactions / daysInQuarterYear;

			YearHalf yearHalf = YearHalf.from(date);
			double surplusesOfHalfYearlySmoothedTransactions = halfYearlySmoothedTransactionsBalancesMap.getOrDefault(yearHalf, 0L).doubleValue();
			long daysInHalfYear = dateRange.getOverlapDays(new DateRange(yearHalf));
			smoothedSurplus += surplusesOfHalfYearlySmoothedTransactions / daysInHalfYear;

			Year year = Year.from(date);
			double surplusesOfYearlySmoothedTransactions = yearlySmoothedTransactionsBalancesMap.getOrDefault(year, 0L).doubleValue();
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
					List<MonthlyStatsDTO> statistics = dateRange.createMonthStream().map(month -> {
						MonthlyStatsDTO monthlyStatsDTO = new MonthlyStatsDTO();
						monthlyStatsDTO.setMonth(month);
						monthlyStatsDTO.setSurplus(0L);
						monthlyStatsDTO.setSmoothedSurplus(0.0);
						monthlyStatsDTO.setTarget(0.0);
						monthlyStatsDTO.setDeviation(0.0);
						return childStatisticNodesMap.get(month).stream()
							.reduce(monthlyStatsDTO, MonthlyStatsDTO::add);
					}).toList();
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

		return dateRange.createMonthStream().map(month -> {
			MonthlyStatsDTO monthlyStatsDTO = new MonthlyStatsDTO(month);
			monthlyStatsDTO.setSurplus(0L);
			monthlyStatsDTO.setSmoothedSurplus(0.0);
			monthlyStatsDTO.setTarget(0.0);
			monthlyStatsDTO.setDeviation(0.0);
			return categoryStatisticsMap.get(month).stream()
				.reduce(monthlyStatsDTO, MonthlyStatsDTO::add);
		}).toList();
	}

	private List<MonthlyStatsDTO> calculateMonthlyStats(List<DailyBalanceProjection> dailyBalances, DateRange dateRange) {
		if (dailyBalances.isEmpty()) {
			return Collections.emptyList();
		}

		Map<YearMonth, Long> monthlyBalancesMap = dailyBalances.stream()
			.collect(Collectors.toMap(
				projection -> YearMonth.from(projection.getDate()),
				DailyBalanceProjection::getBalance,
				Long::sum
			));

		Map<CategorySmoothType, List<DailyBalanceProjection>> dailyBalancesBySmoothType = dailyBalances.stream()
			.collect(Collectors.groupingBy(projection -> projection.getCategory().getSmoothType()));

		Map<YearMonth, Long> dailySmoothedTransactionsBalancesMap = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.DAILY), YearMonth::from);
		Map<YearMonth, Long> monthlySmoothedTransactionsBalancesMap = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.MONTHLY), YearMonth::from);
		Map<YearQuarter, Long> quarterYearlySmoothedTransactionsBalancesMap = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.QUARTER_YEARLY), YearQuarter::from);
		Map<YearHalf, Long> halfYearlySmoothedTransactionsBalancesMap = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.HALF_YEARLY), YearHalf::from);
		Map<Year, Long> yearlySmoothedTransactionsBalancesMap = aggregateBalances(dailyBalancesBySmoothType.get(CategorySmoothType.YEARLY), Year::from);

		return dateRange.createMonthStream().map(month -> {
			MonthlyStatsDTO monthlyStatsDTO = new MonthlyStatsDTO();
			monthlyStatsDTO.setMonth(month);

			long surplus = Optional.ofNullable(monthlyBalancesMap.get(month)).orElse(0L);
			monthlyStatsDTO.setSurplus(surplus);

			double smoothedSurplus = 0.0;

			double surplusesOfDailySmoothedTransactions = dailySmoothedTransactionsBalancesMap.getOrDefault(month, 0L).doubleValue();
			smoothedSurplus += surplusesOfDailySmoothedTransactions;

			double surplusesOfMonthlySmoothedTransactions = monthlySmoothedTransactionsBalancesMap.getOrDefault(month, 0L).doubleValue();
			smoothedSurplus += surplusesOfMonthlySmoothedTransactions;

			YearQuarter yearQuarter = YearQuarter.from(month);
			double surplusesOfQuarterYearlySmoothedTransactions = quarterYearlySmoothedTransactionsBalancesMap.getOrDefault(yearQuarter, 0L).doubleValue();
			long daysInQuarterYear = dateRange.getOverlapMonths(new DateRange(yearQuarter));
			smoothedSurplus += surplusesOfQuarterYearlySmoothedTransactions / daysInQuarterYear;

			YearHalf yearHalf = YearHalf.from(month);
			double surplusesOfHalfYearlySmoothedTransactions = halfYearlySmoothedTransactionsBalancesMap.getOrDefault(yearHalf, 0L).doubleValue();
			long daysInHalfYear = dateRange.getOverlapMonths(new DateRange(yearHalf));
			smoothedSurplus += surplusesOfHalfYearlySmoothedTransactions / daysInHalfYear;

			Year year = Year.from(month);
			double surplusesOfYearlySmoothedTransactions = yearlySmoothedTransactionsBalancesMap.getOrDefault(year, 0L).doubleValue();
			long daysInYear = dateRange.getOverlapMonths(new DateRange(year));
			smoothedSurplus += surplusesOfYearlySmoothedTransactions / daysInYear;

			monthlyStatsDTO.setSmoothedSurplus(smoothedSurplus);

			monthlyStatsDTO.setTarget(0.0); // TODO
			monthlyStatsDTO.setDeviation(0.0); // TODO

			return monthlyStatsDTO;
		}).toList();
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
