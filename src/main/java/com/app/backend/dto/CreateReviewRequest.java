package com.app.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReviewRequest(
		@NotNull @Min(1) @Max(5) Integer rating,
		@NotBlank @Size(max = 120) String title,
		@NotBlank String comment,
		@Size(max = 80) String reviewerName
) {
}

