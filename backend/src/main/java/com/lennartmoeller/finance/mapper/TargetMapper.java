package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.TargetDTO;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.Target;
import com.lennartmoeller.finance.repository.CategoryRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
        componentModel = "spring",
        uses = CategoryMapper.class,
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface TargetMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "startDate", target = "start")
    @Mapping(source = "endDate", target = "end")
    TargetDTO toDto(Target target);

    @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapCategoryIdToCategory")
    @Mapping(source = "start", target = "startDate")
    @Mapping(source = "end", target = "endDate")
    Target toEntity(TargetDTO targetDTO, @Context CategoryRepository repository);

    @Named("mapCategoryIdToCategory")
    default Category mapCategoryIdToCategory(Long categoryId, @Context CategoryRepository repository) {
        return categoryId != null ? repository.findById(categoryId).orElse(null) : null;
    }

    @Named("mapCategoryToCategoryId")
    default Long mapCategoryToCategoryId(Category category) {
        return category != null ? category.getId() : null;
    }
}
