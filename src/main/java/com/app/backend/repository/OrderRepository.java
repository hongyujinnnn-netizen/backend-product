package com.app.backend.repository;

import com.app.backend.model.Order;
import com.app.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

	List<Order> findByUserOrderByCreatedAtDesc(User user);

	List<Order> findAllByOrderByCreatedAtDesc();
}
