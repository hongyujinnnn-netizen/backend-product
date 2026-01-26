package com.app.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record OrderRequest(
	@NotEmpty(message = "Order must contain at least one item")
	List<@Valid OrderItemRequest> items
) {

    public record OrderItemRequest(
	    @NotNull(message = "Product id is required")
	    Long productId,

	    @Positive(message = "Quantity must be greater than zero")
	    int quantity
    ) {
    }
}
