package com.app.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

	private final String secret;
	private final long expirationMillis;
	private Key key;

	public JwtUtil(@Value("${jwt.secret}") String secret,
				   @Value("${jwt.expiration}") long expirationMillis) {
		this.secret = secret;
		this.expirationMillis = expirationMillis;
	}

	@PostConstruct
	void init() {
		byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
		if (keyBytes.length < 32) {
			throw new IllegalArgumentException("JWT secret must be at least 32 bytes");
		}
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public JwtToken generateToken(String username, String role) {
		Instant expiresAt = Instant.now().plusMillis(expirationMillis);
		String token = Jwts.builder()
				.setSubject(username)
				.claim("role", role)
				.setIssuedAt(Date.from(Instant.now()))
				.setExpiration(Date.from(expiresAt))
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
		return new JwtToken(token, expiresAt);
	}

	public String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}

	public String extractRole(String token) {
		return extractAllClaims(token).get("role", String.class);
	}

	public boolean isTokenValid(String token, String username) {
		Claims claims = extractAllClaims(token);
		return username.equals(claims.getSubject()) && claims.getExpiration().toInstant().isAfter(Instant.now());
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	public record JwtToken(String token, Instant expiresAt) {
	}
}
