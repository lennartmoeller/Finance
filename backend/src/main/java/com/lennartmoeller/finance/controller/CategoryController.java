package com.lennartmoeller.finance.controller;

import com.lennartmoeller.finance.dto.CategoryDTO;
import com.lennartmoeller.finance.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

	@GetMapping
	public List<CategoryDTO> getCategories() {
		return categoryService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
		return categoryService.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public CategoryDTO createOrUpdateCategory(@RequestBody CategoryDTO categoryDTO) {
		Optional<CategoryDTO> optionalCategoryDTO = Optional.ofNullable(categoryDTO.getId()).flatMap(categoryService::findById);
		if (optionalCategoryDTO.isEmpty()) {
			categoryDTO.setId(null);
		}
		return categoryService.save(categoryDTO);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
		categoryService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

}
