package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordUpdateResponse {
	private String message;
	private Long userId;
	private String email;
	private int updatedRows;
	private boolean newPasswordVerified;
	private boolean oldPasswordRejected;
	private String backendVersion;
}
