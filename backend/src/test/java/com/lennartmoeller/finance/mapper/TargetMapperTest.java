package com.lennartmoeller.finance.mapper;

import com.lennartmoeller.finance.dto.TargetDTO;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.Target;
import com.lennartmoeller.finance.repository.CategoryRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
	void testNullValues() throws Exception {
		TargetMapperImpl mapper = new TargetMapperImpl();
		assertNull(mapper.toDto(null));
		assertNull(mapper.toEntity(null));

		CategoryRepository repo = mock(CategoryRepository.class);
		Field f = TargetMapper.class.getDeclaredField("categoryRepository");
		f.setAccessible(true);
		f.set(mapper, repo);

		TargetDTO dto = new TargetDTO();
		dto.setCategoryId(1L);
		when(repo.findById(1L)).thenReturn(Optional.empty());

		Target entity = mapper.toEntity(dto);
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
		Field f = TargetMapper.class.getDeclaredField("categoryRepository");
		f.setAccessible(true);
		f.set(mapper, repo);

		Method toEntity = TargetMapper.class.getDeclaredMethod("mapCategoryIdToCategory", Long.class);
		toEntity.setAccessible(true);
		assertSame(cat, toEntity.invoke(mapper, 4L));
		assertNull(toEntity.invoke(mapper, (Object) null));
		Method toId = TargetMapper.class.getDeclaredMethod("mapCategoryToCategoryId", Category.class);
		toId.setAccessible(true);
		assertEquals(4L, toId.invoke(mapper, cat));
		assertNull(toId.invoke(mapper, (Object) null));
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
