package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@RequiredArgsConstructor
@Setter
public class CategoryStatsDTO {

	private CategoryDTO category;
	private RowStatsDTO stats;
	private List<CategoryStatsDTO> children;

}
