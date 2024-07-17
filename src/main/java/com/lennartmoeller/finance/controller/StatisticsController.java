package com.lennartmoeller.finance.controller;

import com.lennartmoeller.finance.dto.DayStatisticsDTO;
import com.lennartmoeller.finance.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stats")
public class StatisticsController {

	private final StatisticsService statisticsService;

	@GetMapping("/days")
	public List<DayStatisticsDTO> getDayStatistics() {
		return statisticsService.getDailyStatistics();
	}

}
