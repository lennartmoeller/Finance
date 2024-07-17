package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.dto.DayStatisticsDTO;
import com.lennartmoeller.finance.projection.DailyBalanceProjection;
import com.lennartmoeller.finance.projection.MonthlyBalanceProjection;
import com.lennartmoeller.finance.projection.YearlyBalanceProjection;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StatisticsService {

	private final AccountRepository accountRepository;
	private final TransactionRepository transactionRepository;

	public static long calculateOverlapDays(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
		LocalDate overlapStart = start1.isAfter(start2) ? start1 : start2;
		LocalDate overlapEnd = end1.isBefore(end2) ? end1 : end2;

		if (overlapStart.isBefore(overlapEnd) || overlapStart.isEqual(overlapEnd)) {
			return ChronoUnit.DAYS.between(overlapStart, overlapEnd) + 1;
		} else {
			return 0;
		}
	}

	public List<DayStatisticsDTO> getDailyStatistics() {
		List<DailyBalanceProjection> dailyBalances = transactionRepository.getDailyBalances();
		if (dailyBalances.isEmpty()) {
			return Collections.emptyList();
		}

		Map<LocalDate, DailyBalanceProjection> dailyBalancesMap = dailyBalances.stream()
			.collect(Collectors.toMap(DailyBalanceProjection::getDate, v -> v));
		Map<LocalDate, DailyBalanceProjection> dailySmoothedMap = transactionRepository.getBalancesForDailySmoothedTransactions().stream()
			.collect(Collectors.toMap(DailyBalanceProjection::getDate, v -> v));
		Map<YearMonth, MonthlyBalanceProjection> monthlySmoothedMap = transactionRepository.getBalancesForMonthlySmoothedTransactions().stream()
			.collect(Collectors.toMap(monthly -> YearMonth.of(monthly.getYear(), monthly.getMonth()), v -> v));
		Map<Year, YearlyBalanceProjection> yearlySmoothedMap = transactionRepository.getBalancesForYearlySmoothedTransactions().stream()
			.collect(Collectors.toMap(yearly -> Year.of(yearly.getYear()), v -> v));

		LocalDate startDate = dailyBalances.getFirst().getDate().withDayOfMonth(1);
		LocalDate endDate = LocalDate.now();
		long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

		long initialBalance = accountRepository.getSummedStartBalance();
		AtomicLong balance = new AtomicLong(initialBalance);
		AtomicLong smoothedBalance = new AtomicLong(initialBalance);

		return Stream.iterate(startDate, date -> date.plusDays(1)).limit(totalDays)
			.map(date -> {
				long surplus = Optional.ofNullable(dailyBalancesMap.get(date)).map(DailyBalanceProjection::getBalance).orElse(0L);
				long smoothedSurplusDaily = Optional.ofNullable(dailySmoothedMap.get(date)).map(DailyBalanceProjection::getBalance).orElse(0L);

				YearMonth yearMonth = YearMonth.from(date);
				long smoothedSurplusMonthly = Optional.ofNullable(monthlySmoothedMap.get(yearMonth)).map(
					v -> {
						long overlappingDays = calculateOverlapDays(startDate, endDate, yearMonth.atDay(1), yearMonth.atEndOfMonth());
						return v.getBalance() / overlappingDays;
					}
				).orElse(0L);

				Year year = Year.from(date);
				long smoothedSurplusYearly = Optional.ofNullable(yearlySmoothedMap.get(Year.from(date)))
					.map(v -> {
						long overlappingDays = calculateOverlapDays(startDate, endDate, year.atDay(1), LocalDate.of(year.getValue(), 12, 31));
						return v.getBalance() / overlappingDays;
					})
					.orElse(0L);

				long smoothedSurplus = smoothedSurplusDaily + smoothedSurplusMonthly + smoothedSurplusYearly;
				return new DayStatisticsDTO(date, balance.addAndGet(surplus), smoothedBalance.addAndGet(smoothedSurplus));
			})
			.collect(Collectors.toList());
	}
}
