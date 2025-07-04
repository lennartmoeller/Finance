package com.lennartmoeller.finance.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.lennartmoeller.finance.dto.TargetDTO;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.Target;
import com.lennartmoeller.finance.repository.CategoryRepository;
import java.lang.reflect.Method;
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

        assertEquals(target.getId(), dto.getId());
        assertEquals(target.getCategory().getId(), dto.getCategoryId());
        assertEquals(target.getAmount(), dto.getAmount());
    }

    @Test
    void testNullValues() {
        TargetMapperImpl mapper = new TargetMapperImpl();
        assertNull(mapper.toDto(null));
        assertNull(mapper.toEntity(null, mock(CategoryRepository.class)));

        CategoryRepository repo = mock(CategoryRepository.class);

        TargetDTO dto = new TargetDTO();
        dto.setCategoryId(1L);
        when(repo.findById(1L)).thenReturn(Optional.empty());

        Target entity = mapper.toEntity(dto, repo);
        assertNull(entity.getCategory());
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
        assertNull(dto.getCategoryId());
    }

    @Test
    void testMappingHelpers() throws Exception {
        CategoryRepository repo = mock(CategoryRepository.class);
        Category cat = new Category();
        cat.setId(4L);
        when(repo.findById(4L)).thenReturn(Optional.of(cat));
        TargetMapperImpl mapper = new TargetMapperImpl();

        Method toEntity =
                TargetMapper.class.getDeclaredMethod("mapCategoryIdToCategory", Long.class, CategoryRepository.class);
        toEntity.setAccessible(true);
        assertSame(cat, toEntity.invoke(mapper, 4L, repo));
        assertNull(toEntity.invoke(mapper, null, repo));
        Method toId = TargetMapper.class.getDeclaredMethod("mapCategoryToCategoryId", Category.class);
        toId.setAccessible(true);
        assertEquals(4L, toId.invoke(mapper, cat));
        assertNull(toId.invoke(mapper, (Object) null));
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

        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getAmount(), entity.getAmount());
        assertSame(category, entity.getCategory());
        verify(repo).findById(7L);
    }
}
