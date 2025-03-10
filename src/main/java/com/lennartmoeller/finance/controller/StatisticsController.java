package com.lennartmoeller.finance.controller;

import com.lennartmoeller.finance.dto.DailySavingStatsDTO;
import com.lennartmoeller.finance.dto.MonthlyCategoryStatsDTO;
import com.lennartmoeller.finance.dto.MonthlySavingStatsDTO;
import com.lennartmoeller.finance.service.DailyBalanceStatsService;
import com.lennartmoeller.finance.service.MonthlyCategoryBalanceStatsService;
import com.lennartmoeller.finance.service.MonthlySavingStatsService;
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
	private final MonthlySavingStatsService monthlySavingStatsService;

	@GetMapping("/dailyBalances")
	public List<DailySavingStatsDTO> getDailyBalances() {
		return dailyBalanceStatsService.getStats();
	}

	@GetMapping("/monthlySavings")
	public List<MonthlySavingStatsDTO> getMonthlySavings() {
		return monthlySavingStatsService.getStats();
	}

	@GetMapping("/monthlyCategoryBalances")
	public MonthlyCategoryStatsDTO getMonthlyCategoryBalances() {
		return monthlyCategoryBalanceStatsService.getStats();
	}

}
