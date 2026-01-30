package com.app.backend.dto;

public record UserResponse(
        Long id,
        String username,
        String email,
        String role
) {
}
