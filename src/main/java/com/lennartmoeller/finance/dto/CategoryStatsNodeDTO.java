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
	CategoryDTO category;
	List<MonthlyStatsDTO> statistics = Collections.emptyList();
	List<CategoryStatsNodeDTO> children = Collections.emptyList();
}
