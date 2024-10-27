package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@RequiredArgsConstructor
@Setter
public class CategoryStatsNodeDTO {

	private CategoryDTO category;
	private RowStatsDTO stats;
	private List<CategoryStatsNodeDTO> children;

}
