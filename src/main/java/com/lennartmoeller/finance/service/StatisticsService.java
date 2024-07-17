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
				long smoothedSurplus = Optional.ofNullable(dailySmoothedMap.get(date)).map(DailyBalanceProjection::getBalance).orElse(0L)
					+ Optional.ofNullable(monthlySmoothedMap.get(YearMonth.from(date))).map(v -> v.getBalance() / date.lengthOfMonth()).orElse(0L)
					+ Optional.ofNullable(yearlySmoothedMap.get(Year.from(date))).map(v -> v.getBalance() / date.lengthOfYear()).orElse(0L);
				return new DayStatisticsDTO(date, balance.addAndGet(surplus), smoothedBalance.addAndGet(smoothedSurplus));
			})
			.collect(Collectors.toList());
	}
}
