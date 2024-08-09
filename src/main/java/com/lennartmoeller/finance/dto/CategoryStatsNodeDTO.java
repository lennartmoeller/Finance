package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class CategoryStatsNodeDTO {
	private CategoryDTO category;
	private RowStatsDTO stats;
	private List<CategoryStatsNodeDTO> children;
}
