package com.ecommerce.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateAddressRequest {

	@Size(max = 120, message = "House / flat number must be 120 characters or less")
	private String houseNo;

	@Size(max = 160, message = "Street / area must be 160 characters or less")
	private String street;

	@Size(max = 80, message = "City must be 80 characters or less")
	private String city;

	@Pattern(regexp = "^$|^[0-9]{6}$", message = "Pincode must be 6 digits")
	private String pincode;

	@Size(max = 80, message = "State must be 80 characters or less")
	private String state;
}
