package com.lennartmoeller.finance.controller;

import com.lennartmoeller.finance.dto.CategoryDTO;
import com.lennartmoeller.finance.mapper.CategoryMapper;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;
	private final CategoryMapper categoryMapper;

	@GetMapping
	public Map<Long, CategoryDTO> getAllCategories() {
		return categoryService.findAll().stream()
			.map(categoryMapper::toDto)
			.collect(Collectors.toMap(CategoryDTO::getId, category -> category));
	}

	@GetMapping("/{id}")
	public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
		return categoryService.findById(id)
			.map(categoryMapper::toDto)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public CategoryDTO createOrUpdateCategory(@RequestBody CategoryDTO categoryDTO) {
		Optional<Category> optionalCategory = Optional.ofNullable(categoryDTO.getId()).flatMap(categoryService::findById);
		if (optionalCategory.isEmpty()) {
			categoryDTO.setId(null);
		}
		return categoryMapper.toDto(categoryService.save(categoryMapper.toEntity(categoryDTO)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
		categoryService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

}
