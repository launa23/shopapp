package com.project.shopapp.repositories;

import com.project.shopapp.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdAndActive(Long userId, boolean active);
    Optional<Order> findByIdAndActive(long id, boolean active);

    List<Order> findAllByActive(boolean active);
}
