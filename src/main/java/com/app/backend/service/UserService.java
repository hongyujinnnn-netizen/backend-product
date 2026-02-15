package com.app.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.backend.dto.UserResponse;
import com.app.backend.model.User;
import com.app.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponse updateUserRole(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setRole(normalizeRole(role));
        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Transactional
    public UserResponse updateUserStatus(Long id, String status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setStatus(normalizeStatus(status));
        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.getStatus());
    }

    private String normalizeRole(String role) {
        String normalized = role == null ? "" : role.trim().toUpperCase();
        if (normalized.startsWith("ROLE_")) {
            normalized = normalized.substring(5);
        }

        return switch (normalized) {
            case "ADMIN" -> "ROLE_ADMIN";
            case "USER" -> "ROLE_USER";
            default -> throw new IllegalArgumentException("Role must be USER or ADMIN");
        };
    }

    private String normalizeStatus(String status) {
        String normalized = status == null ? "" : status.trim().toUpperCase();

        return switch (normalized) {
            case "ACTIVE", "DISABLED", "BANNED" -> normalized;
            default -> throw new IllegalArgumentException("Status must be ACTIVE, DISABLED, or BANNED");
        };
    }
}
