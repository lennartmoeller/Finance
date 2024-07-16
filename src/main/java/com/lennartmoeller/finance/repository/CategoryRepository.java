package com.lennartmoeller.finance.repository;

import com.lennartmoeller.finance.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
