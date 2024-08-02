package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class CategoryStatsNodeDTO {
	private CategoryDTO category;
	private List<MonthlyStatsDTO> statistics;
	private List<CategoryStatsNodeDTO> children;

	public static CategoryStatsNodeDTO empty() {
		CategoryStatsNodeDTO categoryStatsNodeDTO = new CategoryStatsNodeDTO();
		categoryStatsNodeDTO.setStatistics(Collections.emptyList());
		categoryStatsNodeDTO.setChildren(Collections.emptyList());
		return categoryStatsNodeDTO;
	}
}
