package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.CategoryDTO;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.service.CategoryService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {CategoryService.class})
public abstract class CategoryMapper {

	@Autowired
	protected CategoryService categoryService;

	@Mapping(source = "parent.id", target = "parentId")
	public abstract CategoryDTO toDto(Category category);

	@Mapping(target = "parent", expression = "java(categoryService.findById(categoryDTO.getParentId()).orElse(null))")
	public abstract Category toEntity(CategoryDTO categoryDTO);

}
