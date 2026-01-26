package com.app.backend.controller;

import com.app.backend.dto.OrderRequest;
import com.app.backend.model.Order;
import com.app.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@PostMapping
	public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderRequest request, Principal principal) {
		if (principal == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		Order order = orderService.createOrder(request, principal.getName());
		return ResponseEntity.status(HttpStatus.CREATED).body(order);
	}

	@GetMapping
	public ResponseEntity<List<Order>> getMyOrders(Principal principal) {
		if (principal == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		return ResponseEntity.ok(orderService.getOrdersForUser(principal.getName()));
	}

	@GetMapping("/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<Order>> getAllOrders() {
		return ResponseEntity.ok(orderService.getAllOrders());
	}
}
