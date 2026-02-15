package com.app.backend.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.backend.model.User;
import com.app.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		// Remove ROLE_ prefix since .roles() automatically adds it
		String role = user.getRole().startsWith("ROLE_") 
			? user.getRole().substring(5) 
			: user.getRole();

		return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
				.password(user.getPassword())
				.roles(role)
				.disabled(!"ACTIVE".equalsIgnoreCase(user.getStatus()))
				.build();
	}
}
