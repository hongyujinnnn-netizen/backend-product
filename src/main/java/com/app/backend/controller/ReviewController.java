package com.app.backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.backend.dto.CreateReviewRequest;
import com.app.backend.dto.ReviewResponse;
import com.app.backend.dto.ReviewSummaryResponse;
import com.app.backend.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@GetMapping
	public ResponseEntity<List<ReviewResponse>> list(@PathVariable Long productId) {
		return ResponseEntity.ok(reviewService.listApprovedByProduct(productId));
	}

	@GetMapping("/summary")
	public ResponseEntity<ReviewSummaryResponse> summary(@PathVariable Long productId) {
		return ResponseEntity.ok(reviewService.getApprovedSummary(productId));
	}

	@PostMapping
	public ResponseEntity<ReviewResponse> create(@PathVariable Long productId,
			@Valid @RequestBody CreateReviewRequest request,
			Principal principal) {
		if (principal == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		ReviewResponse created = reviewService.create(productId, request, principal.getName());
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}
}

