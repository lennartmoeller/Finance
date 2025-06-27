package com.lennartmoeller.finance.repository;

import com.lennartmoeller.finance.model.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.targets WHERE c.id = :id")
    @NonNull
    Optional<Category> findById(@NonNull Long id);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.targets")
    @NonNull
    List<Category> findAll();
}
