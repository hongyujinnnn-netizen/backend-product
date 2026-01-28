package com.app.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.backend.model.Order;
import com.app.backend.model.User;

public interface OrderRepository extends JpaRepository<Order, Long> {

	List<Order> findByUserOrderByCreatedAtDesc(User user);

	List<Order> findAllByOrderByCreatedAtDesc();
}
