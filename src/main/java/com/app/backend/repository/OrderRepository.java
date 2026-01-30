package com.app.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.backend.model.Order;
import com.app.backend.model.User;

public interface OrderRepository extends JpaRepository<Order, Long> {

	@EntityGraph(attributePaths = {"items", "items.product"})
	List<Order> findByUserOrderByCreatedAtDesc(User user);

	@EntityGraph(attributePaths = {"items", "items.product"})
	List<Order> findByUserUsernameOrderByCreatedAtDesc(String username);

	@EntityGraph(attributePaths = {"items", "items.product"})
	List<Order> findAllByOrderByCreatedAtDesc();
}
