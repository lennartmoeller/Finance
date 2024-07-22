package com.lennartmoeller.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryStatsNodeDTO {
	CategoryDTO category;
	List<MonthlyStatsDTO> statistics;
	List<CategoryStatsNodeDTO> children;
}
