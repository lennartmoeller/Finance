package com.lennartmoeller.finance.dto;

import com.lennartmoeller.finance.model.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryDTO {
	private Long id;
	private Long parent;
	private String label;
	private CategoryType type;
	private Date start;
	private Date end;
	private Long monthlyBudget;
}
