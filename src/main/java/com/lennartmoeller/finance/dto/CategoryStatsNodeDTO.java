package com.lennartmoeller.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryStatsNodeDTO {
	CategoryDTO category;
	List<MonthlyStatsDTO> statistics;
	List<CategoryStatsNodeDTO> children;
}
