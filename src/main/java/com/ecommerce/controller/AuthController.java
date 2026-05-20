package com.ecommerce.controller;

import com.ecommerce.dto.*;
import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

	@Autowired
	private AuthService authService;

	@Autowired
	private UserRepository userRepository;

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.ok(authService.register(request));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	// Protected endpoint — JwtFilter sets authentication before this runs
	// @AuthenticationPrincipal gives us the currently logged-in user
	@GetMapping("/me")
	public ResponseEntity<AuthResponse> me(@AuthenticationPrincipal UserDetails userDetails) {
		User user = userRepository.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));

		return ResponseEntity.ok(new AuthResponse(null, user.getName(), user.getEmail(), user.getRole(), user.getAddress(),
				user.getHouseNo(), user.getStreet(), user.getCity(), user.getPincode(), user.getState(),
				authService.getAddresses(userDetails.getUsername())));
	}

	@PutMapping("/profile")
	public ResponseEntity<AuthResponse> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
			@Valid @RequestBody UpdateProfileRequest request) {
		return ResponseEntity.ok(authService.updateProfile(userDetails.getUsername(), request));
	}

	@PutMapping("/address")
	public ResponseEntity<AuthResponse> updateAddress(@AuthenticationPrincipal UserDetails userDetails,
			@Valid @RequestBody UpdateAddressRequest request) {
		return ResponseEntity.ok(authService.updateAddress(userDetails.getUsername(), request));
	}

	@GetMapping("/addresses")
	public ResponseEntity<java.util.List<AddressResponse>> getAddresses(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(authService.getAddresses(userDetails.getUsername()));
	}

	@PostMapping("/addresses")
	public ResponseEntity<AddressResponse> addAddress(@AuthenticationPrincipal UserDetails userDetails,
			@Valid @RequestBody UpdateAddressRequest request) {
		return ResponseEntity.ok(authService.addAddress(userDetails.getUsername(), request));
	}

	@PutMapping("/addresses/{addressId}")
	public ResponseEntity<AddressResponse> updateSavedAddress(@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable Long addressId, @Valid @RequestBody UpdateAddressRequest request) {
		return ResponseEntity.ok(authService.updateSavedAddress(userDetails.getUsername(), addressId, request));
	}

	@PostMapping("/password/otp")
	public ResponseEntity<OtpResponse> requestPasswordOtp(@Valid @RequestBody OtpRequest request) {
		return ResponseEntity.ok(authService.requestPasswordOtp(request));
	}

	@PutMapping("/password")
	public ResponseEntity<PasswordUpdateResponse> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
		return ResponseEntity.ok(authService.updatePassword(request));
	}

	@PostMapping("/password")
	public ResponseEntity<PasswordUpdateResponse> updatePasswordPost(@Valid @RequestBody UpdatePasswordRequest request) {
		return ResponseEntity.ok(authService.updatePassword(request));
	}

	@PostMapping("/change-password")
	public ResponseEntity<PasswordUpdateResponse> changePassword(@Valid @RequestBody UpdatePasswordRequest request) {
		return ResponseEntity.ok(authService.updatePassword(request));
	}

	@PostMapping("/forgot-password/request-otp")
	public ResponseEntity<OtpResponse> forgotPasswordOtp(@Valid @RequestBody OtpRequest request) {
		return ResponseEntity.ok(authService.requestPasswordOtp(request));
	}

	@PostMapping("/forgot-password/reset")
	public ResponseEntity<PasswordUpdateResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
		return ResponseEntity.ok(authService.resetPassword(request));
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout() {
		return ResponseEntity.noContent().build();
	}
}
