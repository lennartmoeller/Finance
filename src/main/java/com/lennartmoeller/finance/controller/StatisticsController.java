package com.lennartmoeller.finance.controller;

import com.lennartmoeller.finance.dto.DailyBalanceStatsDTO;
import com.lennartmoeller.finance.dto.MonthlyCategoryBalanceStatsDTO;
import com.lennartmoeller.finance.dto.MonthlyInflationCompensationStatsDTO;
import com.lennartmoeller.finance.service.DailyBalanceStatsService;
import com.lennartmoeller.finance.service.MonthlyCategoryBalanceStatsService;
import com.lennartmoeller.finance.service.MonthlyInflationCompensationStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stats")
public class StatisticsController {

	private final DailyBalanceStatsService dailyBalanceStatsService;
	private final MonthlyCategoryBalanceStatsService monthlyCategoryBalanceStatsService;
	private final MonthlyInflationCompensationStatsService monthlyInflationCompensationStatsService;

	@GetMapping("/dailyBalances")
	public List<DailyBalanceStatsDTO> getDailyBalances() {
		return dailyBalanceStatsService.getStats();
	}

	@GetMapping("/monthlyInflationCompensations")
	public List<MonthlyInflationCompensationStatsDTO> getMonthlyInflationCompensation() {
		return monthlyInflationCompensationStatsService.getStats();
	}

	@GetMapping("/monthlyCategoryBalances")
	public MonthlyCategoryBalanceStatsDTO getMonthlyCategoryBalances() {
		return monthlyCategoryBalanceStatsService.getStats();
	}

}
