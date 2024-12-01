package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.dto.MonthlyInflationCompensationStatsDTO;
import com.lennartmoeller.finance.dto.StatsMetricDTO;
import com.lennartmoeller.finance.model.InflationRate;
import com.lennartmoeller.finance.repository.InflationRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class MonthlyInflationCompensationStatsService {

	private final DailyBalanceStatsService dailyBalanceStatsService;
	private final InflationRateRepository inflationRateRepository;

	public List<MonthlyInflationCompensationStatsDTO> getStats() {
		Map<YearMonth, StatsMetricDTO> monthlyMeanBalances = dailyBalanceStatsService.getMonthlyMeanBalances();

		AtomicReference<Double> balance = new AtomicReference<>(0.0);

		return inflationRateRepository.findAll().stream()
			.sorted(Comparator.comparing(InflationRate::getYearMonth))
			.map(inflationRate -> {
				MonthlyInflationCompensationStatsDTO dto = new MonthlyInflationCompensationStatsDTO();

				dto.setYearMonth(inflationRate.getYearMonth());

				double rate = inflationRate.getRate();
				double meanBalance = monthlyMeanBalances.get(inflationRate.getYearMonth()).getRaw();
				double inflation = meanBalance * -rate;
				dto.setInflation(inflation);

				double savings = 0.0; // TODO
				dto.setSavings(savings);

				dto.setBalance(balance.updateAndGet(v -> v + inflation + savings));

				return dto;
			})
			.toList();
	}

}
