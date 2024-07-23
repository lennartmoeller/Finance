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
import com.lennartmoeller.finance.util.TimeUtils;
import com.lennartmoeller.finance.util.YearHalf;
import com.lennartmoeller.finance.util.YearQuarter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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

		LocalDate startDate = dailyBalances.getFirst().getDate().withDayOfMonth(1);
		LocalDate endDate = LocalDate.now();

		YearMonth startMonth = YearMonth.from(startDate);
		YearMonth endMonth = YearMonth.from(endDate);

		List<DailyStatsDTO> dailyStatistics = getDailyStatistics(dailyBalances, startDate, endDate);
		statsDTO.setDailyStats(dailyStatistics);

		List<CategoryStatsNodeDTO> categoryStatistics = getCategoryStatistics(dailyBalances, startMonth, endMonth);
		statsDTO.setCategoryStats(categoryStatistics);

		List<MonthlyStatsDTO> monthlyStatistics = getMonthlyStatistics(categoryStatistics, startMonth, endMonth);
		statsDTO.setMonthlyStats(monthlyStatistics);

		statsDTO.setStartDate(startDate);
		statsDTO.setEndDate(endDate);
		return statsDTO;
	}

	private List<DailyStatsDTO> getDailyStatistics(List<DailyBalanceProjection> dailyBalances, LocalDate startDate, LocalDate endDate) {
		Map<LocalDate, Long> dailyBalancesMap = dailyBalances.stream()
			.collect(Collectors.toMap(
				DailyBalanceProjection::getDate,
				DailyBalanceProjection::getBalance,
				Long::sum
			));

		Map<CategorySmoothType, List<DailyBalanceProjection>> dailyBalancesBySmoothType = dailyBalances.stream()
			.collect(Collectors.groupingBy(projection -> projection.getCategory().getSmoothType()));

		Map<LocalDate, Long> dailySmoothedTransactionsBalancesMap = dailyBalancesBySmoothType.getOrDefault(CategorySmoothType.DAILY, Collections.emptyList()).stream()
			.collect(Collectors.toMap(
				DailyBalanceProjection::getDate,
				DailyBalanceProjection::getBalance,
				Long::sum
			));
		Map<YearMonth, Long> monthlySmoothedTransactionsBalancesMap = dailyBalancesBySmoothType.getOrDefault(CategorySmoothType.MONTHLY, Collections.emptyList()).stream()
			.collect(Collectors.toMap(
				projection -> YearMonth.from(projection.getDate()),
				DailyBalanceProjection::getBalance,
				Long::sum
			));
		Map<YearQuarter, Long> quarterYearlySmoothedTransactionsBalancesMap = dailyBalancesBySmoothType.getOrDefault(CategorySmoothType.QUARTER_YEARLY, Collections.emptyList()).stream()
			.collect(Collectors.toMap(
				projection -> YearQuarter.from(projection.getDate()),
				DailyBalanceProjection::getBalance,
				Long::sum
			));
		Map<YearHalf, Long> halfYearlySmoothedTransactionsBalancesMap = dailyBalancesBySmoothType.getOrDefault(CategorySmoothType.HALF_YEARLY, Collections.emptyList()).stream()
			.collect(Collectors.toMap(
				projection -> YearHalf.from(projection.getDate()),
				DailyBalanceProjection::getBalance,
				Long::sum
			));
		Map<Year, Long> yearlySmoothedTransactionsBalancesMap = dailyBalancesBySmoothType.getOrDefault(CategorySmoothType.YEARLY, Collections.emptyList()).stream()
			.collect(Collectors.toMap(
				projection -> Year.from(projection.getDate()),
				DailyBalanceProjection::getBalance,
				Long::sum
			));

		long initialBalance = accountRepository.getSummedStartBalance();
		AtomicLong balance = new AtomicLong(initialBalance);
		AtomicDouble smoothedBalance = new AtomicDouble(initialBalance);

		return TimeUtils.createDateStream(startDate, endDate).map(date -> {
			DailyStatsDTO dailyStatsDTO = new DailyStatsDTO();
			dailyStatsDTO.setDate(date);

			long surplus = Optional.ofNullable(dailyBalancesMap.get(date)).orElse(0L);
			dailyStatsDTO.setBalance(balance.addAndGet(surplus));

			double smoothedSurplusDaily = Optional.ofNullable(dailySmoothedTransactionsBalancesMap.get(date))
				.map(Double::valueOf)
				.orElse(0.0);

			YearMonth yearMonth = YearMonth.from(date);
			double smoothedSurplusMonthly = Optional.ofNullable(monthlySmoothedTransactionsBalancesMap.get(yearMonth))
				.map(Double::valueOf)
				.map(v -> {
					LocalDate monthStart = yearMonth.atDay(1);
					LocalDate monthEnd = yearMonth.atEndOfMonth();
					long overlappingDays = TimeUtils.calculateOverlapDays(startDate, endDate, monthStart, monthEnd);
					return v / overlappingDays;
				})
				.orElse(0.0);

			YearQuarter yearQuarter = YearQuarter.from(date);
			double smoothedSurplusQuarterYearly = Optional.ofNullable(quarterYearlySmoothedTransactionsBalancesMap.get(yearQuarter))
				.map(Double::valueOf)
				.map(v -> {
					LocalDate quarterStart = yearQuarter.atDay(1);
					LocalDate quarterEnd = yearQuarter.endOfQuarterYear();
					long overlappingDays = TimeUtils.calculateOverlapDays(startDate, endDate, quarterStart, quarterEnd);
					return v / overlappingDays;
				})
				.orElse(0.0);

			YearHalf yearHalf = YearHalf.from(date);
			double smoothedSurplusHalfYearly = Optional.ofNullable(halfYearlySmoothedTransactionsBalancesMap.get(yearHalf))
				.map(Double::valueOf)
				.map(v -> {
					LocalDate halfYearStart = yearHalf.atDay(1);
					LocalDate halfYearEnd = yearHalf.endOfHalfYear();
					long overlappingDays = TimeUtils.calculateOverlapDays(startDate, endDate, halfYearStart, halfYearEnd);
					return v / overlappingDays;
				})
				.orElse(0.0);

			Year year = Year.from(date);
			double smoothedSurplusYearly = Optional.ofNullable(yearlySmoothedTransactionsBalancesMap.get(Year.from(date)))
				.map(Double::valueOf)
				.map(v -> {
					LocalDate yearStart = year.atDay(1);
					LocalDate yearEnd = LocalDate.of(year.getValue(), 12, 31);
					long overlappingDays = TimeUtils.calculateOverlapDays(startDate, endDate, yearStart, yearEnd);
					return v / overlappingDays;
				})
				.orElse(0.0);

			double smoothedSurplus = smoothedSurplusDaily + smoothedSurplusMonthly + smoothedSurplusQuarterYearly + smoothedSurplusHalfYearly + smoothedSurplusYearly;
			dailyStatsDTO.setSmoothedBalance(smoothedBalance.addAndGet(smoothedSurplus));

			return dailyStatsDTO;
		}).toList();
	}

	private List<CategoryStatsNodeDTO> getCategoryStatistics(List<DailyBalanceProjection> dailyBalances, YearMonth startMonth, YearMonth endMonth) {
		List<Category> categories = categoryRepository.findAll();
		return getCategoryStatistics(dailyBalances, startMonth, endMonth, categories, null);
	}

	private List<CategoryStatsNodeDTO> getCategoryStatistics(List<DailyBalanceProjection> dailyBalances, YearMonth startMonth, YearMonth endMonth, List<Category> categories, Category parent) {
		return categories.stream()
			.filter(category -> category.getParent() == parent)
			.sorted(Comparator.comparing(Category::getLabel))
			.map(category -> {
				CategoryStatsNodeDTO categoryStatsNodeDTO = new CategoryStatsNodeDTO();
				categoryStatsNodeDTO.setCategory(categoryMapper.toDto(category));

				// calculate statistic nodes for children
				List<CategoryStatsNodeDTO> childStatisticNodes = getCategoryStatistics(dailyBalances, startMonth, endMonth, categories, category);
				categoryStatsNodeDTO.setChildren(childStatisticNodes);

				// calculate statistics for category
				if (childStatisticNodes.isEmpty()) {
					// leaf node, has own transactions and no children, so calculate own statistics
					List<DailyBalanceProjection> categoriesDailyBalances = dailyBalances.stream()
						.filter(projection -> projection.getCategory().equals(category))
						.toList();
					List<MonthlyStatsDTO> statistics = calculateMonthlyStats(categoriesDailyBalances, startMonth, endMonth);
					categoryStatsNodeDTO.setStatistics(statistics);
				} else {
					// non-leaf node, has no own transactions, so sum up children statistics
					Map<YearMonth, List<MonthlyStatsDTO>> childStatisticNodesMap = childStatisticNodes.stream()
						.flatMap(child -> child.getStatistics().stream())
						.collect(Collectors.groupingBy(MonthlyStatsDTO::getMonth));
					List<MonthlyStatsDTO> statistics = TimeUtils.createMonthStream(startMonth, endMonth).map(month -> {
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

	private List<MonthlyStatsDTO> getMonthlyStatistics(List<CategoryStatsNodeDTO> categoryStatistics, YearMonth startMonth, YearMonth endMonth) {
		Map<YearMonth, List<MonthlyStatsDTO>> categoryStatisticsMap = categoryStatistics.stream()
			.flatMap(x -> x.getStatistics().stream())
			.collect(Collectors.groupingBy(MonthlyStatsDTO::getMonth));

		return TimeUtils.createMonthStream(startMonth, endMonth).map(month -> {
			MonthlyStatsDTO monthlyStatsDTO = new MonthlyStatsDTO(month);
			monthlyStatsDTO.setSurplus(0L);
			monthlyStatsDTO.setSmoothedSurplus(0.0);
			monthlyStatsDTO.setTarget(0.0);
			monthlyStatsDTO.setDeviation(0.0);
			return categoryStatisticsMap.get(month).stream()
				.reduce(monthlyStatsDTO, MonthlyStatsDTO::add);
		}).toList();
	}

	private List<MonthlyStatsDTO> calculateMonthlyStats(List<DailyBalanceProjection> dailyBalances, YearMonth startMonth, YearMonth endMonth) {
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

		Map<YearMonth, Long> dailySmoothedTransactionsBalancesMap = dailyBalancesBySmoothType.getOrDefault(CategorySmoothType.DAILY, Collections.emptyList()).stream()
			.collect(Collectors.toMap(
				projection -> YearMonth.from(projection.getDate()),
				DailyBalanceProjection::getBalance,
				Long::sum
			));
		Map<YearMonth, Long> monthlySmoothedTransactionsBalancesMap = dailyBalancesBySmoothType.getOrDefault(CategorySmoothType.MONTHLY, Collections.emptyList()).stream()
			.collect(Collectors.toMap(
				projection -> YearMonth.from(projection.getDate()),
				DailyBalanceProjection::getBalance,
				Long::sum
			));
		Map<YearQuarter, Long> quarterYearlySmoothedTransactionsBalancesMap = dailyBalancesBySmoothType.getOrDefault(CategorySmoothType.QUARTER_YEARLY, Collections.emptyList()).stream()
			.collect(Collectors.toMap(
				projection -> YearQuarter.from(projection.getDate()),
				DailyBalanceProjection::getBalance,
				Long::sum
			));
		Map<YearHalf, Long> halfYearlySmoothedTransactionsBalancesMap = dailyBalancesBySmoothType.getOrDefault(CategorySmoothType.HALF_YEARLY, Collections.emptyList()).stream()
			.collect(Collectors.toMap(
				projection -> YearHalf.from(projection.getDate()),
				DailyBalanceProjection::getBalance,
				Long::sum
			));
		Map<Year, Long> yearlySmoothedTransactionsBalancesMap = dailyBalancesBySmoothType.getOrDefault(CategorySmoothType.YEARLY, Collections.emptyList()).stream()
			.collect(Collectors.toMap(
				projection -> Year.from(projection.getDate()),
				DailyBalanceProjection::getBalance,
				Long::sum
			));

		return TimeUtils.createMonthStream(startMonth, endMonth).map(month -> {
			MonthlyStatsDTO monthlyStatsDTO = new MonthlyStatsDTO();
			monthlyStatsDTO.setMonth(month);

			long surplus = Optional.ofNullable(monthlyBalancesMap.get(month)).orElse(0L);
			monthlyStatsDTO.setSurplus(surplus);

			double smoothedSurplusDaily = Optional.ofNullable(dailySmoothedTransactionsBalancesMap.get(month))
				.map(Double::valueOf)
				.orElse(0.0);

			double smoothedSurplusMonthly = Optional.ofNullable(monthlySmoothedTransactionsBalancesMap.get(month))
				.map(Double::valueOf)
				.orElse(0.0);

			YearQuarter yearQuarter = YearQuarter.from(month.atDay(1));
			double smoothedSurplusQuarterYearly = Optional.ofNullable(quarterYearlySmoothedTransactionsBalancesMap.get(yearQuarter))
				.map(Double::valueOf)
				.map(v -> {
					long overlappingMonths = TimeUtils.calculateOverlapMonths(
						startMonth,
						endMonth,
						YearMonth.from(yearQuarter.atDay(1)),
						YearMonth.from(yearQuarter.endOfQuarterYear().getMonth())
					);
					return v / overlappingMonths;
				})
				.orElse(0.0);

			YearHalf yearHalf = YearHalf.from(month.atDay(1));
			double smoothedSurplusHalfYearly = Optional.ofNullable(halfYearlySmoothedTransactionsBalancesMap.get(yearHalf))
				.map(Double::valueOf)
				.map(v -> {
					long overlappingMonths = TimeUtils.calculateOverlapMonths(
						startMonth,
						endMonth,
						YearMonth.from(yearHalf.atDay(1)),
						YearMonth.from(yearHalf.endOfHalfYear().getMonth())
					);
					return v / overlappingMonths;
				})
				.orElse(0.0);

			Year year = Year.of(month.getYear());
			double smoothedSurplusYearly = Optional.ofNullable(yearlySmoothedTransactionsBalancesMap.get(year))
				.map(Double::valueOf)
				.map(v -> {
					long overlappingMonths = TimeUtils.calculateOverlapMonths(
						startMonth,
						endMonth,
						year.atMonth(1),
						year.atMonth(12)
					);
					return v / overlappingMonths;
				})
				.orElse(0.0);

			double smoothedSurplus = smoothedSurplusDaily + smoothedSurplusMonthly + smoothedSurplusQuarterYearly + smoothedSurplusHalfYearly + smoothedSurplusYearly;
			monthlyStatsDTO.setSmoothedSurplus(smoothedSurplus);

			monthlyStatsDTO.setTarget(0.0); // TODO
			monthlyStatsDTO.setDeviation(0.0); // TODO

			return monthlyStatsDTO;
		}).toList();
	}

}
