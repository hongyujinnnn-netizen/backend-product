package com.app.backend.service;

import com.app.backend.dto.LoginRequest;
import com.app.backend.dto.LoginResponse;
import com.app.backend.dto.RegisterRequest;
import com.app.backend.model.User;
import com.app.backend.repository.UserRepository;
import com.app.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Transactional
	public LoginResponse register(RegisterRequest request) {
		if (userRepository.existsByUsername(request.username())) {
			throw new IllegalArgumentException("Username is already taken");
		}
		if (userRepository.existsByEmail(request.email())) {
			throw new IllegalArgumentException("Email is already registered");
		}

		User user = User.builder()
				.username(request.username())
				.email(request.email())
				.password(passwordEncoder.encode(request.password()))
				.role("ROLE_USER")
				.build();
		userRepository.save(user);

		JwtUtil.JwtToken token = jwtUtil.generateToken(user.getUsername(), user.getRole());
		return new LoginResponse(token.token(), token.expiresAt(), "Bearer");
	}

	@Transactional(readOnly = true)
	public LoginResponse login(LoginRequest request) {
		User user = userRepository.findByUsername(request.username())
				.orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new IllegalArgumentException("Invalid credentials");
		}

		JwtUtil.JwtToken token = jwtUtil.generateToken(user.getUsername(), user.getRole());
		return new LoginResponse(token.token(), token.expiresAt(), "Bearer");
	}
}
