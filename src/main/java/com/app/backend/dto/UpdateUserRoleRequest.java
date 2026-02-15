package com.app.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRoleRequest(
        @NotBlank(message = "Role is required")
        String role
) {
}
