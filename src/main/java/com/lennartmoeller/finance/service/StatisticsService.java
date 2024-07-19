package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.dto.CategoryStatsNodeDTO;
import com.lennartmoeller.finance.dto.DailyStatsDTO;
import com.lennartmoeller.finance.dto.MonthlyStatsDTO;
import com.lennartmoeller.finance.dto.StatsDTO;
import com.lennartmoeller.finance.projection.DailyBalanceProjection;
import com.lennartmoeller.finance.projection.MonthlyBalanceProjection;
import com.lennartmoeller.finance.projection.YearlyBalanceProjection;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import com.lennartmoeller.finance.util.LocalDateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
	private final TransactionRepository transactionRepository;

	public StatsDTO getStatistics() {
		StatsDTO stats = new StatsDTO();
		stats.setDailyStats(getDailyStatistics());
		stats.setMonthlyStats(getMonthlyStatistics());
		stats.setCategoryStats(getCategoryStatistics());
		return stats;
	}

	private List<DailyStatsDTO> getDailyStatistics() {
		List<DailyBalanceProjection> dailyBalances = transactionRepository.getDailyBalances();
		if (dailyBalances.isEmpty()) {
			return Collections.emptyList();
		}

		Map<LocalDate, DailyBalanceProjection> dailyBalancesMap = dailyBalances.stream().collect(Collectors.toMap(DailyBalanceProjection::getDate, v -> v));
		Map<LocalDate, DailyBalanceProjection> dailySmoothedMap = transactionRepository.getBalancesForDailySmoothedTransactions().stream().collect(Collectors.toMap(DailyBalanceProjection::getDate, v -> v));
		Map<YearMonth, MonthlyBalanceProjection> monthlySmoothedMap = transactionRepository.getBalancesForMonthlySmoothedTransactions().stream().collect(Collectors.toMap(monthly -> YearMonth.of(monthly.getYear(), monthly.getMonth()), v -> v));
		Map<Year, YearlyBalanceProjection> yearlySmoothedMap = transactionRepository.getBalancesForYearlySmoothedTransactions().stream().collect(Collectors.toMap(yearly -> Year.of(yearly.getYear()), v -> v));

		LocalDate startDate = dailyBalances.getFirst().getDate().withDayOfMonth(1);
		LocalDate endDate = LocalDate.now();

		long initialBalance = accountRepository.getSummedStartBalance();
		AtomicLong balance = new AtomicLong(initialBalance);
		AtomicLong smoothedBalance = new AtomicLong(initialBalance);

		return LocalDateUtils.createDateStream(startDate, endDate).map(date -> {
			long surplus = Optional.ofNullable(dailyBalancesMap.get(date)).map(DailyBalanceProjection::getBalance).orElse(0L);
			long smoothedSurplusDaily = Optional.ofNullable(dailySmoothedMap.get(date)).map(DailyBalanceProjection::getBalance).orElse(0L);

			YearMonth yearMonth = YearMonth.from(date);
			long smoothedSurplusMonthly = Optional.ofNullable(monthlySmoothedMap.get(yearMonth)).map(v -> {
				long overlappingDays = LocalDateUtils.calculateOverlapDays(startDate, endDate, yearMonth.atDay(1), yearMonth.atEndOfMonth());
				return v.getBalance() / overlappingDays;
			}).orElse(0L);

			Year year = Year.from(date);
			long smoothedSurplusYearly = Optional.ofNullable(yearlySmoothedMap.get(Year.from(date))).map(v -> {
				long overlappingDays = LocalDateUtils.calculateOverlapDays(startDate, endDate, year.atDay(1), LocalDate.of(year.getValue(), 12, 31));
				return v.getBalance() / overlappingDays;
			}).orElse(0L);

			long smoothedSurplus = smoothedSurplusDaily + smoothedSurplusMonthly + smoothedSurplusYearly;
			return new DailyStatsDTO(date, balance.addAndGet(surplus), smoothedBalance.addAndGet(smoothedSurplus));
		}).toList();
	}

	private List<MonthlyStatsDTO> getMonthlyStatistics() {
		return Collections.emptyList();
	}

	private List<CategoryStatsNodeDTO> getCategoryStatistics() {
		return Collections.emptyList();
	}

}
