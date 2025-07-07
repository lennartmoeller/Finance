package com.lennartmoeller.finance.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.TargetDTO;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.Target;
import com.lennartmoeller.finance.repository.CategoryRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class TargetMapperTest {
    @Test
    void testToDto() {
        Category category = new Category();
        category.setId(3L);

        Target target = new Target();
        target.setId(5L);
        target.setCategory(category);
        target.setAmount(100L);

        TargetMapper mapper = new TargetMapperImpl();
        TargetDTO dto = mapper.toDto(target);

        assertThat(dto.getId()).isEqualTo(target.getId());
        assertThat(dto.getCategoryId()).isEqualTo(target.getCategory().getId());
        assertThat(dto.getAmount()).isEqualTo(target.getAmount());
    }

    @Test
    void testNullValues() {
        TargetMapperImpl mapper = new TargetMapperImpl();
        assertThat(mapper.toDto(null)).isNull();
        assertThat(mapper.toEntity(null, mock(CategoryRepository.class))).isNull();

        CategoryRepository repo = mock(CategoryRepository.class);

        TargetDTO dto = new TargetDTO();
        dto.setCategoryId(1L);
        when(repo.findById(1L)).thenReturn(Optional.empty());

        Target entity = mapper.toEntity(dto, repo);
        assertThat(entity.getCategory()).isNull();
        verify(repo).findById(1L);
    }

    @Test
    void testToDtoWithNullCategory() {
        Target target = new Target();
        target.setId(9L);
        target.setAmount(5L);
        // category null
        TargetMapper mapper = new TargetMapperImpl();
        TargetDTO dto = mapper.toDto(target);
        assertThat(dto.getCategoryId()).isNull();
    }

    @Test
    void testMappingHelpers() {
        CategoryRepository repo = mock(CategoryRepository.class);
        Category cat = new Category();
        cat.setId(4L);
        when(repo.findById(4L)).thenReturn(Optional.of(cat));
        TargetMapperImpl mapper = new TargetMapperImpl();

        assertThat(mapper.mapCategoryIdToCategory(4L, repo)).isSameAs(cat);
        assertThat(mapper.mapCategoryIdToCategory(null, repo)).isNull();
        assertThat(mapper.mapCategoryToCategoryId(cat)).isEqualTo(4L);
        assertThat(mapper.mapCategoryToCategoryId(null)).isNull();
    }

    @Test
    void testToEntityUsesRepository() {
        CategoryRepository repo = mock(CategoryRepository.class);
        Category category = new Category();
        category.setId(7L);
        when(repo.findById(7L)).thenReturn(Optional.of(category));

        TargetMapperImpl mapper = new TargetMapperImpl();

        TargetDTO dto = new TargetDTO();
        dto.setId(8L);
        dto.setCategoryId(7L);
        dto.setAmount(200L);

        Target entity = mapper.toEntity(dto, repo);

        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getAmount()).isEqualTo(dto.getAmount());
        assertThat(entity.getCategory()).isSameAs(category);
        verify(repo).findById(7L);
    }
}
