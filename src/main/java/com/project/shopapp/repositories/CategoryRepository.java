package com.project.shopapp.repositories;

import com.project.shopapp.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByIdAndActive(long id, boolean active);

    List<Category> findAllByActive(boolean active);

    boolean existsByNameAndActive(String name, boolean active);
    List<Category> findAllByIdIsNotAndActive(long id, boolean active);

}
