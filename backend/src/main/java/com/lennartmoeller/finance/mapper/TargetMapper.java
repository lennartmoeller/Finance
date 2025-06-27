package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.TargetDTO;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.Target;
import com.lennartmoeller.finance.repository.CategoryRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public abstract class TargetMapper {

    @Autowired
    private CategoryRepository categoryRepository;

    @Mapping(source = "category.id", target = "categoryId")
    public abstract TargetDTO toDto(Target target);

    @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapCategoryIdToCategory")
    public abstract Target toEntity(TargetDTO targetDTO);

    @Named("mapCategoryIdToCategory")
    Category mapCategoryIdToCategory(Long categoryId) {
        return categoryId != null ? categoryRepository.findById(categoryId).orElse(null) : null;
    }

    @Named("mapCategoryToCategoryId")
    Long mapCategoryToCategoryId(Category category) {
        return category != null ? category.getId() : null;
    }
}
