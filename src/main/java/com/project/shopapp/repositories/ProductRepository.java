package com.project.shopapp.repositories;

import com.project.shopapp.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);

    Optional<Product> findByIdAndActive(long id, boolean active);
    boolean existsByName(String name);
//    Phân trang
    Page<Product> findAllByActive(Pageable pageable, boolean active);
    Page<Product> findByCategoryIdAndActive(Pageable pageable, long id, boolean active);
}
