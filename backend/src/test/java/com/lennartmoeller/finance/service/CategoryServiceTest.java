package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.dto.CategoryDTO;
import com.lennartmoeller.finance.mapper.CategoryMapper;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

	private CategoryRepository categoryRepository;
	private CategoryMapper categoryMapper;
	private CategoryService categoryService;

	private static Category makeCategory(Long id, Category parent) {
		Category c = new Category();
		c.setId(id);
		c.setParent(parent);
		return c;
	}

	@BeforeEach
	void setUp() {
		categoryRepository = mock(CategoryRepository.class);
		categoryMapper = mock(CategoryMapper.class);
		categoryService = new CategoryService(categoryRepository, categoryMapper);
	}

	@Test
	void testFindAll() {
		Category c1 = new Category();
		c1.setId(1L);
		Category c2 = new Category();
		c2.setId(2L);
		when(categoryRepository.findAll()).thenReturn(List.of(c1, c2));

		CategoryDTO d1 = new CategoryDTO();
		CategoryDTO d2 = new CategoryDTO();
		when(categoryMapper.toDto(c1)).thenReturn(d1);
		when(categoryMapper.toDto(c2)).thenReturn(d2);

		List<CategoryDTO> result = categoryService.findAll();

		assertEquals(List.of(d1, d2), result);
		verify(categoryRepository).findAll();
		verify(categoryMapper).toDto(c1);
		verify(categoryMapper).toDto(c2);
	}

	@Test
	void testFindByIdFound() {
		Category c = new Category();
		c.setId(1L);
		CategoryDTO dto = new CategoryDTO();
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(c));
		when(categoryMapper.toDto(c)).thenReturn(dto);

		Optional<CategoryDTO> result = categoryService.findById(1L);

		assertTrue(result.isPresent());
		assertEquals(dto, result.get());
	}

	@Test
	void testFindByIdNotFound() {
		when(categoryRepository.findById(42L)).thenReturn(Optional.empty());

		Optional<CategoryDTO> result = categoryService.findById(42L);

		assertTrue(result.isEmpty());
		verifyNoInteractions(categoryMapper);
	}

	@Test
	void testSave() {
		CategoryDTO input = new CategoryDTO();
		Category entity = new Category();
		Category saved = new Category();
		CategoryDTO output = new CategoryDTO();

		when(categoryMapper.toEntity(input)).thenReturn(entity);
		when(categoryRepository.save(entity)).thenReturn(saved);
		when(categoryMapper.toDto(saved)).thenReturn(output);

		CategoryDTO result = categoryService.save(input);

		assertEquals(output, result);
	}

	@Test
	void testDeleteById() {
		categoryService.deleteById(5L);
		verify(categoryRepository).deleteById(5L);
	}

	@Test
	void testCollectChildCategoryIdsRecursivelyNullOrEmpty() {
		assertNull(categoryService.collectChildCategoryIdsRecursively(null));
		assertEquals(List.of(), categoryService.collectChildCategoryIdsRecursively(new ArrayList<>()));
		verifyNoInteractions(categoryRepository);
	}

	@Test
	void testCollectChildCategoryIdsRecursivelyHierarchy() {
		Category root1 = makeCategory(1L, null);
		Category root2 = makeCategory(2L, null);
		Category child3 = makeCategory(3L, root1);
		Category child4 = makeCategory(4L, root1);
		Category child5 = makeCategory(5L, child3);
		Category child6 = makeCategory(6L, root2);

		when(categoryRepository.findAll()).thenReturn(List.of(root1, root2, child3, child4, child5, child6));

		List<Long> result = categoryService.collectChildCategoryIdsRecursively(List.of(1L));

		assertEquals(List.of(1L, 3L, 4L, 5L), result);
	}
}
