package com.ecommerce.service;

import com.ecommerce.dto.*;

public interface AuthService {
	AuthResponse register(RegisterRequest request);

	AuthResponse login(LoginRequest request);

	AuthResponse updateProfile(String currentEmail, UpdateProfileRequest request);

	PasswordUpdateResponse updatePassword(UpdatePasswordRequest request);
}
