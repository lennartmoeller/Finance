package com.lennartmoeller.finance.service;

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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

	private final AccountRepository accountRepository;
	private final CategoryRepository categoryRepository;
	private final TransactionRepository transactionRepository;
	private final CategoryMapper categoryMapper;

	@Transactional(readOnly = true)
	public StatsDTO getStatistics() {
		List<DailyBalanceProjection> dailyBalances = transactionRepository.getDailyBalances();
		if (dailyBalances.isEmpty()) {
			return new StatsDTO(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
		}

		List<Category> rootCategories = categoryRepository.findAll();

		return new StatsDTO(
			getDailyStatistics(dailyBalances),
			getCategoryStatistics(rootCategories, dailyBalances),
			getMonthlyStatistics(dailyBalances)
		);
	}

	private List<DailyStatsDTO> getDailyStatistics(List<DailyBalanceProjection> dailyBalances) {
		Map<LocalDate, Long> dailyBalancesMap = dailyBalances.stream()
			.collect(Collectors.toMap(
				DailyBalanceProjection::getDate,
				DailyBalanceProjection::getBalance,
				Long::sum
			));
		Map<LocalDate, Long> dailySmoothedTransactionsBalancesMap = dailyBalances.stream()
			.filter(projection -> projection.getCategory().getSmoothType().equals(CategorySmoothType.DAILY))
			.collect(Collectors.toMap(
				DailyBalanceProjection::getDate,
				DailyBalanceProjection::getBalance,
				Long::sum
			));
		Map<YearMonth, Long> monthlySmoothedTransactionsBalancesMap = dailyBalances.stream()
			.filter(projection -> projection.getCategory().getSmoothType().equals(CategorySmoothType.MONTHLY))
			.collect(Collectors.toMap(
				projection -> YearMonth.from(projection.getDate()),
				DailyBalanceProjection::getBalance,
				Long::sum
			));
		Map<YearQuarter, Long> quarterYearlySmoothedTransactionsBalancesMap = dailyBalances.stream()
			.filter(projection -> projection.getCategory().getSmoothType().equals(CategorySmoothType.QUARTER_YEARLY))
			.collect(Collectors.toMap(
				projection -> YearQuarter.from(projection.getDate()),
				DailyBalanceProjection::getBalance,
				Long::sum
			));
		Map<YearHalf, Long> halfYearlySmoothedTransactionsBalancesMap = dailyBalances.stream()
			.filter(projection -> projection.getCategory().getSmoothType().equals(CategorySmoothType.HALF_YEARLY))
			.collect(Collectors.toMap(
				projection -> YearHalf.from(projection.getDate()),
				DailyBalanceProjection::getBalance,
				Long::sum
			));
		Map<Year, Long> yearlySmoothedTransactionsBalancesMap = dailyBalances.stream()
			.filter(projection -> projection.getCategory().getSmoothType().equals(CategorySmoothType.YEARLY))
			.collect(Collectors.toMap(
				projection -> Year.from(projection.getDate()),
				DailyBalanceProjection::getBalance,
				Long::sum
			));

		LocalDate startDate = dailyBalances.getFirst().getDate().withDayOfMonth(1);
		LocalDate endDate = LocalDate.now();

		long initialBalance = accountRepository.getSummedStartBalance();
		AtomicLong balance = new AtomicLong(initialBalance);
		AtomicLong smoothedBalance = new AtomicLong(initialBalance);

		return TimeUtils.createDateStream(startDate, endDate).map(date -> {
			long surplus = Optional.ofNullable(dailyBalancesMap.get(date)).orElse(0L);

			long smoothedSurplusDaily = Optional.ofNullable(dailySmoothedTransactionsBalancesMap.get(date)).orElse(0L);

			YearMonth yearMonth = YearMonth.from(date);
			long smoothedSurplusMonthly = Optional.ofNullable(monthlySmoothedTransactionsBalancesMap.get(yearMonth))
				.map(v -> {
					LocalDate monthStart = yearMonth.atDay(1);
					LocalDate monthEnd = yearMonth.atEndOfMonth();
					long overlappingDays = TimeUtils.calculateOverlapDays(startDate, endDate, monthStart, monthEnd);
					return v / overlappingDays;
				})
				.orElse(0L);

			YearQuarter yearQuarter = YearQuarter.from(date);
			long smoothedSurplusQuarterYearly = Optional.ofNullable(quarterYearlySmoothedTransactionsBalancesMap.get(yearQuarter))
				.map(v -> {
					LocalDate quarterStart = yearQuarter.atDay(1);
					LocalDate quarterEnd = yearQuarter.endOfQuarterYear();
					long overlappingDays = TimeUtils.calculateOverlapDays(startDate, endDate, quarterStart, quarterEnd);
					return v / overlappingDays;
				})
				.orElse(0L);

			YearHalf yearHalf = YearHalf.from(date);
			long smoothedSurplusHalfYearly = Optional.ofNullable(halfYearlySmoothedTransactionsBalancesMap.get(yearHalf))
				.map(v -> {
					LocalDate halfYearStart = yearHalf.atDay(1);
					LocalDate halfYearEnd = yearHalf.endOfHalfYear();
					long overlappingDays = TimeUtils.calculateOverlapDays(startDate, endDate, halfYearStart, halfYearEnd);
					return v / overlappingDays;
				})
				.orElse(0L);

			Year year = Year.from(date);
			long smoothedSurplusYearly = Optional.ofNullable(yearlySmoothedTransactionsBalancesMap.get(Year.from(date)))
				.map(v -> {
					LocalDate yearStart = year.atDay(1);
					LocalDate yearEnd = LocalDate.of(year.getValue(), 12, 31);
					long overlappingDays = TimeUtils.calculateOverlapDays(startDate, endDate, yearStart, yearEnd);
					return v / overlappingDays;
				})
				.orElse(0L);

			long smoothedSurplus = smoothedSurplusDaily + smoothedSurplusMonthly + smoothedSurplusQuarterYearly + smoothedSurplusHalfYearly + smoothedSurplusYearly;
			return new DailyStatsDTO(date, balance.addAndGet(surplus), smoothedBalance.addAndGet(smoothedSurplus));
		}).toList();
	}

	private List<MonthlyStatsDTO> getMonthlyStatistics(List<DailyBalanceProjection> dailyBalances) {
		if (dailyBalances.isEmpty()) {
			return Collections.emptyList();
		}

		Map<YearMonth, Long> monthlyBalancesMap = dailyBalances.stream()
			.collect(Collectors.toMap(
				projection -> YearMonth.from(projection.getDate()),
				DailyBalanceProjection::getBalance,
				Long::sum
			));
		Map<YearMonth, Long> monthlySmoothedTransactionsBalancesMap = dailyBalances.stream()
			.filter(projection -> projection.getCategory().getSmoothType().equals(CategorySmoothType.DAILY) || projection.getCategory().getSmoothType().equals(CategorySmoothType.MONTHLY))
			.collect(Collectors.toMap(
				projection -> YearMonth.from(projection.getDate()),
				DailyBalanceProjection::getBalance,
				Long::sum
			));
		Map<YearQuarter, Long> quarterYearlySmoothedTransactionsBalancesMap = dailyBalances.stream()
			.filter(projection -> projection.getCategory().getSmoothType().equals(CategorySmoothType.QUARTER_YEARLY))
			.collect(Collectors.toMap(
				projection -> YearQuarter.from(projection.getDate()),
				DailyBalanceProjection::getBalance,
				Long::sum
			));
		Map<YearHalf, Long> halfYearlySmoothedTransactionsBalancesMap = dailyBalances.stream()
			.filter(projection -> projection.getCategory().getSmoothType().equals(CategorySmoothType.HALF_YEARLY))
			.collect(Collectors.toMap(
				projection -> YearHalf.from(projection.getDate()),
				DailyBalanceProjection::getBalance,
				Long::sum
			));
		Map<Year, Long> yearlySmoothedTransactionsBalancesMap = dailyBalances.stream()
			.filter(projection -> projection.getCategory().getSmoothType().equals(CategorySmoothType.YEARLY))
			.collect(Collectors.toMap(
				projection -> Year.from(projection.getDate()),
				DailyBalanceProjection::getBalance,
				Long::sum
			));

		YearMonth startMonth = YearMonth.from(dailyBalances.getFirst().getDate());
		YearMonth endMonth = YearMonth.now();

		return TimeUtils.createMonthStream(startMonth, endMonth).map(month -> {
			long surplus = Optional.ofNullable(monthlyBalancesMap.get(month)).orElse(0L);

			long smoothedSurplusMonthly = Optional.ofNullable(monthlySmoothedTransactionsBalancesMap.get(month)).orElse(0L);

			YearQuarter yearQuarter = YearQuarter.from(month.atDay(1));
			long smoothedSurplusQuarterYearly = Optional.ofNullable(quarterYearlySmoothedTransactionsBalancesMap.get(yearQuarter))
				.map(v -> {
					long overlappingMonths = TimeUtils.calculateOverlapMonths(
						startMonth,
						endMonth,
						YearMonth.from(yearQuarter.atDay(1)),
						YearMonth.from(yearQuarter.endOfQuarterYear().getMonth())
					);
					return v / overlappingMonths;
				})
				.orElse(0L);

			YearHalf yearHalf = YearHalf.from(month.atDay(1));
			long smoothedSurplusHalfYearly = Optional.ofNullable(halfYearlySmoothedTransactionsBalancesMap.get(yearHalf))
				.map(v -> {
					long overlappingMonths = TimeUtils.calculateOverlapMonths(
						startMonth,
						endMonth,
						YearMonth.from(yearHalf.atDay(1)),
						YearMonth.from(yearHalf.endOfHalfYear().getMonth())
					);
					return v / overlappingMonths;
				})
				.orElse(0L);

			Year year = Year.of(month.getYear());
			long smoothedSurplusYearly = Optional.ofNullable(yearlySmoothedTransactionsBalancesMap.get(year))
				.map(v -> {
					long overlappingMonths = TimeUtils.calculateOverlapMonths(
						startMonth,
						endMonth,
						year.atMonth(1),
						year.atMonth(12)
					);
					return v / overlappingMonths;
				})
				.orElse(0L);

			long smoothedSurplus = smoothedSurplusMonthly + smoothedSurplusQuarterYearly + smoothedSurplusHalfYearly + smoothedSurplusYearly;
			return new MonthlyStatsDTO(month, surplus, smoothedSurplus, 0L, 0L);
		}).toList();
	}

	private List<CategoryStatsNodeDTO> getCategoryStatistics(List<Category> rootCategories, List<DailyBalanceProjection> dailyBalances) {
		return rootCategories.stream()
			.map(category -> {
				List<DailyBalanceProjection> categoriesDailyBalances = dailyBalances.stream()
					.filter(projection -> projection.getCategory() == category).toList();

				return new CategoryStatsNodeDTO(
					categoryMapper.toDto(category),
					getMonthlyStatistics(categoriesDailyBalances),
					getCategoryStatistics(category.getChildren(), dailyBalances)
				);
			})
			.toList();
	}

}
