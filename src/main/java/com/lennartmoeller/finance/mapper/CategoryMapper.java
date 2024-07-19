package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.CategoryDTO;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.repository.CategoryRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class CategoryMapper {

	@Autowired
	private CategoryRepository categoryRepository;

	@Mapping(source = "parent.id", target = "parentId")
	public abstract CategoryDTO toDto(Category category);

	@Mapping(target = "parent", source = "parentId")
	public abstract Category toEntity(CategoryDTO categoryDTO);

	Long mapParentToParentId(Category parent) {
		return parent != null ? parent.getId() : null;
	}

	Category mapParentIdToParent(Long parentId) {
		return parentId != null ? categoryRepository.findById(parentId).orElse(null) : null;
	}

}
