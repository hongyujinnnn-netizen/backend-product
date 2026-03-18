package com.app.backend.dto;

public record ReviewSummaryResponse(
		Long productId,
		double averageRating,
		long reviewCount,
		long count1,
		long count2,
		long count3,
		long count4,
		long count5
) {
}

