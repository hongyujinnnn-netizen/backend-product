package com.app.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.backend.dto.OrderRequest;
import com.app.backend.model.Order;
import com.app.backend.model.OrderItem;
import com.app.backend.model.Product;
import com.app.backend.model.User;
import com.app.backend.repository.OrderRepository;
import com.app.backend.repository.ProductRepository;
import com.app.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	@Transactional
	public Order createOrder(OrderRequest request, String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		if (request.items() == null || request.items().isEmpty()) {
			throw new IllegalArgumentException("Order must contain at least one item");
		}

		Order order = new Order();
		order.setUser(user);
		order.setUsername(user.getUsername());
		order.setUserEmail(user.getEmail());

		BigDecimal total = BigDecimal.ZERO;

		for (OrderRequest.OrderItemRequest itemRequest : request.items()) {
			Product product = productRepository.findById(itemRequest.productId())
					.orElseThrow(() -> new IllegalArgumentException("Product not found"));

			if (product.getStock() < itemRequest.quantity()) {
				throw new IllegalArgumentException("Insufficient stock for product " + product.getName());
			}

			product.setStock(product.getStock() - itemRequest.quantity());

			OrderItem item = OrderItem.builder()
					.order(order)
					.product(product)
					.quantity(itemRequest.quantity())
					.price(product.getPrice())
					.build();

			order.getItems().add(item);
			total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity())));
		}

		order.setTotal(total);
		return orderRepository.save(order);
	}

	@Transactional(readOnly = true)
	public List<Order> getOrdersForUser(String username, Integer limit) {
		// Fetch newest-first by username without requiring caller to load User entity
		List<Order> orders = orderRepository.findByUserUsernameOrderByCreatedAtDesc(username);
		if (orders.isEmpty()) {
			// Keep behavior consistent with previous flow: throw if user missing
			userRepository.findByUsername(username)
					.orElseThrow(() -> new IllegalArgumentException("User not found"));
		}
		if (limit != null && limit > 0) {
			return orders.stream().limit(limit).toList();
		}
		return orders;
	}

	@Transactional(readOnly = true)
	public List<Order> getAllOrders() {
		return orderRepository.findAllByOrderByCreatedAtDesc();
	}
}
