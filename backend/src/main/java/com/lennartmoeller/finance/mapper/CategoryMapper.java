package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.CategoryDTO;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.repository.CategoryRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
        componentModel = "spring",
        uses = TargetMapper.class,
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface CategoryMapper {

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "targets", target = "targets")
    CategoryDTO toDto(Category category);

    @Mapping(target = "parent", source = "parentId", qualifiedByName = "mapParentIdToParent")
    @Mapping(target = "targets", source = "targets")
    Category toEntity(CategoryDTO dto, @Context CategoryRepository repository);

    @Named("mapParentIdToParent")
    default Category mapParentIdToParent(Long parentId, @Context CategoryRepository repository) {
        return parentId != null ? repository.findById(parentId).orElse(null) : null;
    }

    @Named("mapParentToParentId")
    default Long mapParentToParentId(Category parent) {
        return parent != null ? parent.getId() : null;
    }
}
