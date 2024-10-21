package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.dto.CategoryDTO;
import com.lennartmoeller.finance.mapper.CategoryMapper;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final CategoryMapper categoryMapper;

	public List<CategoryDTO> findAll() {
		return categoryRepository.findAll().stream()
			.map(categoryMapper::toDto)
			.toList();
	}

	public Optional<CategoryDTO> findById(Long id) {
		return categoryRepository.findById(id).map(categoryMapper::toDto);
	}

	public CategoryDTO save(CategoryDTO categoryDTO) {
		Category category = categoryMapper.toEntity(categoryDTO);
		Category savedCategory = categoryRepository.save(category);
		return categoryMapper.toDto(savedCategory);
	}

	public void deleteById(Long id) {
		categoryRepository.deleteById(id);
	}

}
