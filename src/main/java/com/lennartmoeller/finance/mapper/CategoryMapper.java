package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.CategoryDTO;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryMapper {

	private final CategoryService categoryService;

	public CategoryDTO toDto(Category category) {
		Long parentId = Optional.ofNullable(category.getParent()).map(Category::getId).orElse(null);

		return new CategoryDTO(
			category.getId(),
			parentId,
			category.getLabel(),
			category.getTransactionType(),
			category.getSmoothType(),
			category.getStart(),
			category.getEnd(),
			category.getTarget()
		);
	}

	public Category toEntity(CategoryDTO categoryDTO) {
		Category parent = Optional.ofNullable(categoryDTO.getParentId())
			.map(parentId -> categoryService.findById(categoryDTO.getParentId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid parent category ID"))
			)
			.orElse(null);

		return new Category(
			categoryDTO.getId(),
			parent,
			categoryDTO.getLabel(),
			categoryDTO.getTransactionType(),
			categoryDTO.getSmoothType(),
			categoryDTO.getStart(),
			categoryDTO.getEnd(),
			categoryDTO.getTarget()
		);
	}

}
