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
	private List<MonthlyStatsDTO> statistics = Collections.emptyList();
	private List<CategoryStatsNodeDTO> children = Collections.emptyList();
}
