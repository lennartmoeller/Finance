package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.dto.CategoryDTO;
import com.lennartmoeller.finance.mapper.CategoryMapper;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.repository.CategoryRepository;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll().stream().map(categoryMapper::toDto).toList();
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

    public @Nullable List<Long> collectChildCategoryIdsRecursively(@Nullable List<Long> rootCategoryIds) {
        if (rootCategoryIds == null || rootCategoryIds.isEmpty()) {
            return rootCategoryIds;
        }

        Map<Long, List<Long>> parentToChildIdsMap = categoryRepository.findAll().stream()
                .filter(category -> category.getParent() != null)
                .collect(Collectors.groupingBy(
                        category -> category.getParent().getId(),
                        Collectors.mapping(Category::getId, Collectors.toList())));

        Set<Long> visited = new LinkedHashSet<>();
        Deque<Long> queue = new ArrayDeque<>(rootCategoryIds);

        while (!queue.isEmpty()) {
            Long current = queue.poll();
            if (visited.add(current)) {
                queue.addAll(parentToChildIdsMap.getOrDefault(current, List.of()));
            }
        }

        return new ArrayList<>(visited);
    }
}
