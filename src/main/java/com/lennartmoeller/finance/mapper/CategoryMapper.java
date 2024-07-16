package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.CategoryDTO;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryMapper {

	private final CategoryService categoryService;

	public CategoryDTO toDto(Category category) {
		Long parentId = category.getParent() != null ? category.getParent().getId() : null;
		return new CategoryDTO(
			category.getId(),
			parentId,
			category.getLabel(),
			category.getType(),
			category.getStart(),
			category.getEnd(),
			category.getMonthlyBudget()
		);
	}

	public Category toEntity(CategoryDTO categoryDTO) {
		Category parent = categoryDTO.getParent() != null ?
			categoryService.findById(categoryDTO.getParent())
				.orElseThrow(() -> new IllegalArgumentException("Invalid parent category ID")) : null;

		return new Category(
			categoryDTO.getId(),
			parent,
			categoryDTO.getLabel(),
			categoryDTO.getType(),
			categoryDTO.getStart(),
			categoryDTO.getEnd(),
			categoryDTO.getMonthlyBudget()
		);
	}
}
