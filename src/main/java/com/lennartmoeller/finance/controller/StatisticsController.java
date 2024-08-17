package com.lennartmoeller.finance.controller;

import com.lennartmoeller.finance.dto.StatsDTO;
import com.lennartmoeller.finance.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stats")
public class StatisticsController {

	private final StatisticsService statisticsService;

	@GetMapping
	public StatsDTO getStatistics() throws InterruptedException {
		Thread.sleep(5000); // TODO: Testing purposes only, remove this line
		return statisticsService.getStatistics();
	}

}
