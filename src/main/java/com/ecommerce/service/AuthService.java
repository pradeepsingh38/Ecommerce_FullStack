package com.ecommerce.service;

import com.ecommerce.dto.*;

public interface AuthService {
	AuthResponse register(RegisterRequest request);

	AuthResponse login(LoginRequest request);

	AuthResponse updateProfile(String currentEmail, UpdateProfileRequest request);

	AuthResponse updateAddress(String currentEmail, UpdateAddressRequest request);

	java.util.List<AddressResponse> getAddresses(String currentEmail);

	AddressResponse addAddress(String currentEmail, UpdateAddressRequest request);

	AddressResponse updateSavedAddress(String currentEmail, Long addressId, UpdateAddressRequest request);

	OtpResponse requestPasswordOtp(OtpRequest request);

	PasswordUpdateResponse updatePassword(UpdatePasswordRequest request);

	PasswordUpdateResponse resetPassword(ResetPasswordRequest request);
}
