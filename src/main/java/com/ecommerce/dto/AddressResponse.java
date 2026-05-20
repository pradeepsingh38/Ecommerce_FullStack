package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddressResponse {
	private Long addressId;
	private String houseNo;
	private String street;
	private String city;
	private String pincode;
	private String state;
	private String fullAddress;
	private Boolean defaultAddress;
	private String source;
}
