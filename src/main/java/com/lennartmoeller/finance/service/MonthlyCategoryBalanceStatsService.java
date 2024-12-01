package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.dto.*;
import com.lennartmoeller.finance.mapper.CategoryMapper;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.CategorySmoothType;
import com.lennartmoeller.finance.model.TransactionType;
import com.lennartmoeller.finance.projection.DailyBalanceProjection;
import com.lennartmoeller.finance.repository.CategoryRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import com.lennartmoeller.finance.util.DateRange;
import com.lennartmoeller.finance.util.YearHalf;
import com.lennartmoeller.finance.util.YearQuarter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.lennartmoeller.finance.util.AggregationUtils.aggregateBalances;

@Service
@RequiredArgsConstructor
public class MonthlyCategoryBalanceStatsService {

	private final CategoryMapper categoryMapper;
	private final CategoryRepository categoryRepository;
	private final TransactionRepository transactionRepository;

	public MonthlyCategoryBalanceStatsDTO getStats() {
		List<DailyBalanceProjection> dailyBalances = transactionRepository.getDailyBalances();

		if (dailyBalances.isEmpty()) {
			return MonthlyCategoryBalanceStatsDTO.empty();
		}

		DateRange dateRange = new DateRange(
			dailyBalances.getFirst().getDate().withDayOfMonth(1),
			LocalDate.now()
		);

		MonthlyCategoryBalanceStatsDTO monthlyCategoryBalanceStatsDTO = new MonthlyCategoryBalanceStatsDTO();

		List<TransactionTypeCategoryStatsDTO> categoryStatsByType = getCategoryStatsByType(dailyBalances, dateRange);
		monthlyCategoryBalanceStatsDTO.setStats(categoryStatsByType);

		monthlyCategoryBalanceStatsDTO.setStartDate(dateRange.getStartDate());
		monthlyCategoryBalanceStatsDTO.setEndDate(dateRange.getEndDate());

		return monthlyCategoryBalanceStatsDTO;
	}

	private List<TransactionTypeCategoryStatsDTO> getCategoryStatsByType(List<DailyBalanceProjection> dailyBalances, DateRange dateRange) {
		Map<TransactionType, List<Category>> categoriesByType = categoryRepository.findAll().stream()
			.collect(Collectors.groupingBy(Category::getTransactionType));

		return Arrays.stream(TransactionType.values())
			.map(transactionType -> {
				List<Category> categories = categoriesByType.getOrDefault(transactionType, List.of());
				if (categories.isEmpty()) {
					return TransactionTypeCategoryStatsDTO.empty(transactionType, dateRange);
				}
				List<CategoryStatsDTO> categoryStats = getCategoryStats(dailyBalances, dateRange, categories, null);
				return new TransactionTypeCategoryStatsDTO(transactionType, categoryStats, dateRange);
			})
			.toList();
	}

	private List<CategoryStatsDTO> getCategoryStats(List<DailyBalanceProjection> dailyBalances, DateRange dateRange, List<Category> categories, Category parent) {
		return categories.stream()
			.filter(category -> category.getParent() == parent)
			.map(category -> {
				CategoryStatsDTO categoryStatsDTO = new CategoryStatsDTO();
				categoryStatsDTO.setCategory(categoryMapper.toDto(category));

				// calculate statistic nodes for children
				List<CategoryStatsDTO> childStatisticNodes = getCategoryStats(dailyBalances, dateRange, categories, category);
				categoryStatsDTO.setChildren(childStatisticNodes);

				// calculate statistics for category
				if (childStatisticNodes.isEmpty()) {
					// leaf node, has own transactions and no children, so calculate own statistics
					List<DailyBalanceProjection> categoriesDailyBalances = dailyBalances.stream()
						.filter(projection -> projection.getCategory().equals(category))
						.toList();
					Map<YearMonth, CellStatsDTO> monthlyStats = calculateMonthlyCategoryStats(category, categoriesDailyBalances, dateRange);
					categoryStatsDTO.setStats(new RowStatsDTO(monthlyStats));
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
								CellStatsDTO cellStatsDTO = CellStatsDTO.add(categoryStatsMap.get(month));
								rawSurplusesDS.addValue(cellStatsDTO.getSurplus().getRaw());
								smoothedSurplusesDS.addValue(cellStatsDTO.getSurplus().getSmoothed());
								return cellStatsDTO;
							}
						));

					// calculate performance
					ImmutableTriple<Double, Double, Double> rawBounds = PerformanceDTO.calculateBounds(rawSurplusesDS, category.getTransactionType());
					ImmutableTriple<Double, Double, Double> smoothedBounds = PerformanceDTO.calculateBounds(smoothedSurplusesDS, category.getTransactionType());
					monthlyStats.forEach((month, cellStatsDTO) -> cellStatsDTO.calculatePerformance(rawBounds, smoothedBounds));

					categoryStatsDTO.setStats(new RowStatsDTO(monthlyStats));
				}

				return categoryStatsDTO;
			})
			.sorted(Comparator.comparing(v -> -Math.abs(v.getStats().getMean().getSurplus().getSmoothed())))
			.toList();
	}

	private Map<YearMonth, CellStatsDTO> calculateMonthlyCategoryStats(Category category, List<DailyBalanceProjection> dailyBalances, DateRange dateRange) {
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
		ImmutableTriple<Double, Double, Double> rawBounds = PerformanceDTO.calculateBounds(rawSurplusesDS, category.getTransactionType());
		ImmutableTriple<Double, Double, Double> smoothedBounds = PerformanceDTO.calculateBounds(smoothedSurplusesDS, category.getTransactionType());
		monthlyCategoryStats.forEach((month, cellStats) -> cellStats.calculatePerformance(rawBounds, smoothedBounds));

		return monthlyCategoryStats;
	}

}
