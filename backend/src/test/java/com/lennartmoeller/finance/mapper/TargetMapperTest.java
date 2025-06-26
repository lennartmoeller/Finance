package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.TargetDTO;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.Target;
import com.lennartmoeller.finance.repository.CategoryRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void testToEntityUsesRepository() throws Exception {
        CategoryRepository repo = mock(CategoryRepository.class);
        Category category = new Category();
        category.setId(7L);
        when(repo.findById(7L)).thenReturn(Optional.of(category));

        TargetMapperImpl mapper = new TargetMapperImpl();
        Field f = TargetMapper.class.getDeclaredField("categoryRepository");
        f.setAccessible(true);
        f.set(mapper, repo);

        TargetDTO dto = new TargetDTO();
        dto.setId(8L);
        dto.setCategoryId(7L);
        dto.setAmount(200L);

        Target entity = mapper.toEntity(dto);

        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getAmount(), entity.getAmount());
        assertSame(category, entity.getCategory());
        verify(repo).findById(7L);
    }
}
