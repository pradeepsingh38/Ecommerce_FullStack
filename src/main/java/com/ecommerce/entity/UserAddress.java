package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_addresses")
@Data
public class UserAddress {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long addressId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false, length = 120)
	private String houseNo;

	@Column(length = 160)
	private String street;

	@Column(nullable = false, length = 80)
	private String city;

	@Column(nullable = false, length = 6)
	private String pincode;

	@Column(nullable = false, length = 80)
	private String state;

	@Column(nullable = false, length = 500)
	private String fullAddress;

	@Column(length = 500)
	private String originalFullAddress;

	@Column(nullable = false)
	private Boolean defaultAddress = false;
}
