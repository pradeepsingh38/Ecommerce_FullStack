package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String role = "USER";

	@Column(length = 500)
	private String address;

	@Column(length = 120)
	private String houseNo;

	@Column(length = 160)
	private String street;

	@Column(length = 80)
	private String city;

	@Column(length = 6)
	private String pincode;

	@Column(length = 80)
	private String state;
}
