package com.lennartmoeller.finance.repository;

import com.lennartmoeller.finance.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	@Query("""
		    SELECT c
		    FROM Category c
		    WHERE c.parent IS NULL
		    ORDER BY c.label
		""")
	List<Category> findRoots();

}
