package com.ecommerce.repository;

import com.ecommerce.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findAllByOrderByCreatedAtDesc();

	List<Order> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
}
