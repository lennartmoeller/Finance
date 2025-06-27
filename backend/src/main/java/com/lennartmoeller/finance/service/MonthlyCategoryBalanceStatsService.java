package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.dto.*;
import com.lennartmoeller.finance.mapper.CategoryMapper;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.TransactionType;
import com.lennartmoeller.finance.projection.DailyBalanceProjection;
import com.lennartmoeller.finance.repository.CategoryRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import com.lennartmoeller.finance.util.DateRange;
import com.lennartmoeller.finance.util.smoother.SmootherMonthly;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MonthlyCategoryBalanceStatsService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public MonthlyCategoryStatsDTO getStats() {
        List<DailyBalanceProjection> dailyBalances = transactionRepository.getDailyBalances();

        if (dailyBalances.isEmpty()) {
            return MonthlyCategoryStatsDTO.empty();
        }

        DateRange dateRange = new DateRange(dailyBalances.getFirst().getDate().withDayOfMonth(1), LocalDate.now());

        MonthlyCategoryStatsDTO monthlyCategoryStatsDTO = new MonthlyCategoryStatsDTO();

        Map<TransactionType, TransactionTypeStatsDTO> categoryStatsByType =
                getCategoryStatsByType(dailyBalances, dateRange);
        monthlyCategoryStatsDTO.setStats(categoryStatsByType);

        monthlyCategoryStatsDTO.setStartDate(dateRange.getStartDate());
        monthlyCategoryStatsDTO.setEndDate(dateRange.getEndDate());

        return monthlyCategoryStatsDTO;
    }

    private Map<TransactionType, TransactionTypeStatsDTO> getCategoryStatsByType(
            List<DailyBalanceProjection> dailyBalances, DateRange dateRange) {
        Map<TransactionType, List<Category>> categoriesByType =
                categoryRepository.findAll().stream().collect(Collectors.groupingBy(Category::getTransactionType));

        return Arrays.stream(TransactionType.values())
                .collect(Collectors.toMap(Function.identity(), transactionType -> {
                    List<Category> categories = categoriesByType.getOrDefault(transactionType, List.of());
                    if (categories.isEmpty()) {
                        return TransactionTypeStatsDTO.empty(dateRange);
                    }
                    List<CategoryStatsDTO> categoryStats = getCategoryStats(dailyBalances, dateRange, categories, null);
                    return new TransactionTypeStatsDTO(categoryStats, dateRange);
                }));
    }

    private List<CategoryStatsDTO> getCategoryStats(
            List<DailyBalanceProjection> dailyBalances,
            DateRange dateRange,
            List<Category> categories,
            Category parent) {
        return categories.stream()
                .filter(category -> category.getParent() == parent)
                .map(category -> {
                    CategoryStatsDTO categoryStatsDTO = new CategoryStatsDTO();
                    categoryStatsDTO.setCategory(categoryMapper.toDto(category));

                    // calculate statistic nodes for children
                    List<CategoryStatsDTO> childStatisticNodes =
                            getCategoryStats(dailyBalances, dateRange, categories, category);
                    categoryStatsDTO.setChildren(childStatisticNodes);

                    // calculate statistics for category
                    if (childStatisticNodes.isEmpty()) {
                        // leaf node, has own transactions and no children, so calculate own statistics
                        List<DailyBalanceProjection> categoriesDailyBalances = dailyBalances.stream()
                                .filter(projection -> projection.getCategory().equals(category))
                                .toList();
                        Map<YearMonth, CellStatsDTO> monthlyStats =
                                calculateMonthlyCategoryStats(category, categoriesDailyBalances, dateRange);
                        categoryStatsDTO.setStats(new RowStatsDTO(monthlyStats));
                    } else {
                        // non-leaf node, has no own transactions, so sum up children statistics
                        Map<YearMonth, List<CellStatsDTO>> categoryStatsMap = childStatisticNodes.stream()
                                .flatMap(child -> child.getStats().getMonthly().entrySet().stream())
                                .collect(Collectors.groupingBy(
                                        Map.Entry::getKey,
                                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

                        DescriptiveStatistics rawSurplusesDS = new DescriptiveStatistics();
                        DescriptiveStatistics smoothedSurplusesDS = new DescriptiveStatistics();

                        Map<YearMonth, CellStatsDTO> monthlyStats = dateRange
                                .createMonthStream()
                                .collect(Collectors.toMap(month -> month, month -> {
                                    CellStatsDTO cellStatsDTO = CellStatsDTO.add(categoryStatsMap.get(month));
                                    rawSurplusesDS.addValue(
                                            cellStatsDTO.getSurplus().getRaw());
                                    smoothedSurplusesDS.addValue(
                                            cellStatsDTO.getSurplus().getSmoothed());
                                    return cellStatsDTO;
                                }));

                        // calculate performance
                        ImmutableTriple<Double, Double, Double> rawBounds =
                                PerformanceDTO.calculateBounds(rawSurplusesDS, category.getTransactionType());
                        ImmutableTriple<Double, Double, Double> smoothedBounds =
                                PerformanceDTO.calculateBounds(smoothedSurplusesDS, category.getTransactionType());
                        monthlyStats.forEach(
                                (month, cellStatsDTO) -> cellStatsDTO.calculatePerformance(rawBounds, smoothedBounds));

                        categoryStatsDTO.setStats(new RowStatsDTO(monthlyStats));
                    }

                    return categoryStatsDTO;
                })
                .sorted(Comparator.comparing(
                        v -> -Math.abs(v.getStats().getMean().getSurplus().getSmoothed())))
                .toList();
    }

    private Map<YearMonth, CellStatsDTO> calculateMonthlyCategoryStats(
            Category category, List<DailyBalanceProjection> dailyBalances, DateRange dateRange) {
        SmootherMonthly smoother = new SmootherMonthly();
        dailyBalances.forEach(projection ->
                smoother.add(YearMonth.from(projection.getDate()), category.getSmoothType(), projection.getBalance()));

        DescriptiveStatistics rawSurplusesDS = new DescriptiveStatistics();
        DescriptiveStatistics smoothedSurplusesDS = new DescriptiveStatistics();

        Map<YearMonth, CellStatsDTO> monthlyCategoryStats = dateRange
                .createMonthStream()
                .collect(Collectors.toMap(Function.identity(), yearMonth -> {
                    CellStatsDTO cellStats = new CellStatsDTO();

                    StatsMetricDTO surplus = smoother.get(yearMonth);
                    cellStats.setSurplus(surplus);
                    rawSurplusesDS.addValue(surplus.getRaw());
                    smoothedSurplusesDS.addValue(surplus.getSmoothed());

                    DateRange monthRange = DateRange.getOverlapRange(dateRange, new DateRange(yearMonth));
                    long daysInMonth = monthRange.getDays();
                    double target = category.getTargets().stream()
                            .mapToDouble(t -> {
                                double amount = t.getAmount().doubleValue();
                                long overlapDays =
                                        monthRange.getOverlapDays(new DateRange(t.getStartDate(), t.getEndDate()));
                                return amount / daysInMonth * overlapDays;
                            })
                            .sum();
                    cellStats.setTarget(target);

                    return cellStats;
                }));

        // calculate performance
        ImmutableTriple<Double, Double, Double> rawBounds =
                PerformanceDTO.calculateBounds(rawSurplusesDS, category.getTransactionType());
        ImmutableTriple<Double, Double, Double> smoothedBounds =
                PerformanceDTO.calculateBounds(smoothedSurplusesDS, category.getTransactionType());
        monthlyCategoryStats.forEach((month, cellStats) -> cellStats.calculatePerformance(rawBounds, smoothedBounds));

        return monthlyCategoryStats;
    }
}
