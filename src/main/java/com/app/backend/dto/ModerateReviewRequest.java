package com.app.backend.dto;

import com.app.backend.model.ReviewStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ModerateReviewRequest(
		@NotNull ReviewStatus status,
		@Size(max = 500) String reason
) {
}

