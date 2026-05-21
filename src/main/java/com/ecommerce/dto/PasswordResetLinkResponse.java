package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordResetLinkResponse {
	private String message;
	private String email;
	private long expiresInMinutes;
}
