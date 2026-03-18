package com.app.backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.backend.dto.ModerateReviewRequest;
import com.app.backend.dto.ReviewResponse;
import com.app.backend.model.ReviewStatus;
import com.app.backend.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

	private final ReviewService reviewService;

	@GetMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<ReviewResponse>> list(
			@RequestParam(value = "status", required = false) ReviewStatus status,
			@RequestParam(value = "productId", required = false) Long productId,
			@RequestParam(value = "rating", required = false) Integer rating) {
		return ResponseEntity.ok(reviewService.adminList(status, productId, rating));
	}

	@PatchMapping("/{reviewId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<ReviewResponse> moderate(@PathVariable Long reviewId,
			@Valid @RequestBody ModerateReviewRequest request,
			Principal principal) {
		ReviewResponse updated = reviewService.moderate(reviewId, request, principal != null ? principal.getName() : null);
		return ResponseEntity.ok(updated);
	}
}

