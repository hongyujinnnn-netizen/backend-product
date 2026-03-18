package com.app.backend.dto;

import java.time.Instant;

import com.app.backend.model.ReviewStatus;

public record ReviewResponse(
		Long id,
		Long productId,
		Integer rating,
		String title,
		String comment,
		String reviewerName,
		Instant createdAt,
		boolean verifiedPurchase,
		ReviewStatus status,
		long helpfulCount
) {
}

