package com.ecommerce.service.impl;

import com.ecommerce.dto.*;
import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.security.JwtUtil;
import com.ecommerce.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Lazy // ← this one line fixes the circular dependency
	@Autowired
	private AuthenticationManager authenticationManager;

	@Override
	public AuthResponse register(RegisterRequest request) {
		String email = request.getEmail().trim().toLowerCase();

		if (userRepository.findByEmail(email).isPresent()) {
			throw new RuntimeException("Email already in use");
		}

		User user = new User();
		user.setName(request.getName().trim());
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole("USER");

		userRepository.save(user);

		String token = jwtUtil.generateToken(user.getEmail());
		return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole());
	}

	@Override
	public AuthResponse login(LoginRequest request) {
		String email = request.getEmail().trim().toLowerCase();
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(email, request.getPassword()));

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));

		String token = jwtUtil.generateToken(user.getEmail());
		return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole());
	}

	@Override
	public AuthResponse updateProfile(String currentEmail, UpdateProfileRequest request) {
		User user = userRepository.findByEmail(currentEmail).orElseThrow(() -> new RuntimeException("User not found"));
		String email = request.getEmail().trim().toLowerCase();

		userRepository.findByEmail(email).ifPresent(existingUser -> {
			if (!existingUser.getUserId().equals(user.getUserId())) {
				throw new RuntimeException("Email already in use");
			}
		});

		user.setName(request.getName().trim());
		user.setEmail(email);
		userRepository.save(user);

		String token = jwtUtil.generateToken(user.getEmail());
		return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole());
	}

	@Override
	@Transactional
	public PasswordUpdateResponse updatePassword(UpdatePasswordRequest request) {
		String email = request.getEmail().trim().toLowerCase();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
			throw new RuntimeException("Current password is incorrect");
		}

		if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
			throw new RuntimeException("New password must be different from current password");
		}

		String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
		int updatedRows = jdbcTemplate.update("UPDATE users SET password = ? WHERE user_id = ?", encodedNewPassword,
				user.getUserId());
		if (updatedRows != 1) {
			throw new RuntimeException("Password update failed. Please try again");
		}

		String savedPassword = jdbcTemplate.queryForObject("SELECT password FROM users WHERE user_id = ?", String.class,
				user.getUserId());

		boolean newPasswordVerified = passwordEncoder.matches(request.getNewPassword(), savedPassword);
		boolean oldPasswordStillWorks = passwordEncoder.matches(request.getCurrentPassword(), savedPassword);

		if (!newPasswordVerified) {
			throw new RuntimeException("Password update failed. Please try again");
		}

		if (oldPasswordStillWorks) {
			throw new RuntimeException("Password was not changed in database");
		}

		return new PasswordUpdateResponse("Password updated successfully", user.getUserId(), user.getEmail(), updatedRows,
				newPasswordVerified, !oldPasswordStillWorks, "password-update-v3-user-id-jdbc");
	}
}
