package com.lennartmoeller.finance.dto;

import com.lennartmoeller.finance.model.Category;
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
	Category category;
	List<MonthlyStatsDTO> statistics;
	List<CategoryStatsNodeDTO> children;
}
