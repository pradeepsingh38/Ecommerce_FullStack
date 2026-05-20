package com.ecommerce.service.impl;

import com.ecommerce.dto.*;
import com.ecommerce.entity.User;
import com.ecommerce.entity.UserAddress;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserAddressRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.security.JwtUtil;
import com.ecommerce.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthServiceImpl implements AuthService {

	private static final long OTP_EXPIRY_MINUTES = 5;
	private static final SecureRandom OTP_RANDOM = new SecureRandom();
	private final Map<String, OtpEntry> passwordOtps = new ConcurrentHashMap<>();

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserAddressRepository userAddressRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private JavaMailSender mailSender;

	@Value("${app.mail.from:}")
	private String mailFrom;

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
		return mapToAuthResponse(token, user);
	}

	@Override
	public AuthResponse login(LoginRequest request) {
		String email = request.getEmail().trim().toLowerCase();
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(email, request.getPassword()));

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));

		String token = jwtUtil.generateToken(user.getEmail());
		return mapToAuthResponse(token, user);
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
		return mapToAuthResponse(token, user);
	}

	@Override
	public AuthResponse updateAddress(String currentEmail, UpdateAddressRequest request) {
		User user = userRepository.findByEmail(currentEmail).orElseThrow(() -> new RuntimeException("User not found"));
		UserAddress address = applyAddress(new UserAddress(), request);
		address.setUser(user);
		address.setDefaultAddress(true);
		userAddressRepository.save(address);
		syncUserDefaultAddress(user, address);
		userRepository.save(user);

		return mapToAuthResponse(null, user);
	}

	@Override
	public List<AddressResponse> getAddresses(String currentEmail) {
		User user = userRepository.findByEmail(currentEmail).orElseThrow(() -> new RuntimeException("User not found"));
		migrateLegacyAddress(user);
		return getAddressResponses(user.getUserId());
	}

	@Override
	public AddressResponse addAddress(String currentEmail, UpdateAddressRequest request) {
		User user = userRepository.findByEmail(currentEmail).orElseThrow(() -> new RuntimeException("User not found"));
		List<UserAddress> existingAddresses = userAddressRepository
				.findByUser_UserIdOrderByDefaultAddressDescAddressIdDesc(user.getUserId());
		UserAddress address = applyAddress(new UserAddress(), request);
		address.setUser(user);
		address.setDefaultAddress(existingAddresses.isEmpty());
		UserAddress savedAddress = userAddressRepository.save(address);

		if (savedAddress.getDefaultAddress()) {
			syncUserDefaultAddress(user, savedAddress);
			userRepository.save(user);
		}

		return mapAddress(savedAddress);
	}

	@Override
	public AddressResponse updateSavedAddress(String currentEmail, Long addressId, UpdateAddressRequest request) {
		User user = userRepository.findByEmail(currentEmail).orElseThrow(() -> new RuntimeException("User not found"));
		UserAddress address = userAddressRepository.findByAddressIdAndUser_UserId(addressId, user.getUserId())
				.orElseThrow(() -> new RuntimeException("Address not found"));
		applyAddress(address, request);
		UserAddress savedAddress = userAddressRepository.save(address);

		if (savedAddress.getDefaultAddress()) {
			syncUserDefaultAddress(user, savedAddress);
			userRepository.save(user);
		}

		return mapAddress(savedAddress);
	}

	@Override
	public OtpResponse requestPasswordOtp(OtpRequest request) {
		String email = request.getEmail().trim().toLowerCase();
		userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		String otp = String.valueOf(100000 + OTP_RANDOM.nextInt(900000));
		sendPasswordOtpEmail(email, otp);
		passwordOtps.put(email, new OtpEntry(otp, LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES)));

		return new OtpResponse("Verification OTP sent to your email", email, OTP_EXPIRY_MINUTES);
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

		verifyOtp(email, request.getOtp());

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

	@Override
	@Transactional
	public PasswordUpdateResponse resetPassword(ResetPasswordRequest request) {
		String email = request.getEmail().trim().toLowerCase();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
		verifyOtp(email, request.getOtp());

		if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
			throw new RuntimeException("New password must be different from current password");
		}

		String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
		int updatedRows = jdbcTemplate.update("UPDATE users SET password = ? WHERE user_id = ?", encodedNewPassword,
				user.getUserId());
		if (updatedRows != 1) {
			throw new RuntimeException("Password reset failed. Please try again");
		}

		String savedPassword = jdbcTemplate.queryForObject("SELECT password FROM users WHERE user_id = ?", String.class,
				user.getUserId());
		boolean newPasswordVerified = passwordEncoder.matches(request.getNewPassword(), savedPassword);

		if (!newPasswordVerified) {
			throw new RuntimeException("Password reset failed. Please try again");
		}

		return new PasswordUpdateResponse("Password reset successfully", user.getUserId(), user.getEmail(), updatedRows,
				true, true, "forgot-password-otp-v1");
	}

	private void verifyOtp(String email, String otp) {
		OtpEntry entry = passwordOtps.get(email);
		if (entry == null) {
			throw new RuntimeException("Please request an OTP first");
		}
		if (entry.expiresAt().isBefore(LocalDateTime.now())) {
			passwordOtps.remove(email);
			throw new RuntimeException("OTP expired. Please request a new OTP");
		}
		if (!entry.otp().equals(otp.trim())) {
			throw new RuntimeException("Invalid OTP");
		}
		passwordOtps.remove(email);
	}

	private record OtpEntry(String otp, LocalDateTime expiresAt) {
	}

	private void sendPasswordOtpEmail(String email, String otp) {
		if (mailFrom == null || mailFrom.isBlank()) {
			throw new RuntimeException("Mail sender is not configured. Set MAIL_USERNAME, MAIL_PASSWORD, and MAIL_FROM.");
		}

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(mailFrom);
		message.setTo(email);
		message.setSubject("Your ShopEase password verification OTP");
		message.setText("""
				Your ShopEase OTP is: %s

				This OTP expires in %d minutes.
				If you did not request this, you can ignore this email.
				""".formatted(otp, OTP_EXPIRY_MINUTES));
		mailSender.send(message);
	}

	private AuthResponse mapToAuthResponse(String token, User user) {
		return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole(), user.getAddress(),
				user.getHouseNo(), user.getStreet(), user.getCity(), user.getPincode(), user.getState(),
				getAddressResponses(user.getUserId()));
	}

	private String clean(String value) {
		return value == null ? "" : value.trim();
	}

	private UserAddress applyAddress(UserAddress address, UpdateAddressRequest request) {
		String houseNo = clean(request.getHouseNo());
		String street = clean(request.getStreet());
		String city = clean(request.getCity());
		String pincode = clean(request.getPincode());
		String state = clean(request.getState());

		if (houseNo.isBlank() || city.isBlank() || pincode.isBlank() || state.isBlank()) {
			throw new RuntimeException("House / flat no, city, pincode, and state are required");
		}

		address.setHouseNo(houseNo);
		address.setStreet(street);
		address.setCity(city);
		address.setPincode(pincode);
		address.setState(state);
		address.setFullAddress(buildAddress(houseNo, street, city, pincode, state));
		return address;
	}

	private String buildAddress(String houseNo, String street, String city, String pincode, String state) {
		StringBuilder address = new StringBuilder(houseNo);
		if (!street.isBlank()) {
			address.append(", ").append(street);
		}
		address.append(", ").append(city).append(", ").append(pincode).append(", ").append(state);
		return address.toString();
	}

	private void syncUserDefaultAddress(User user, UserAddress address) {
		user.setHouseNo(address.getHouseNo());
		user.setStreet(address.getStreet());
		user.setCity(address.getCity());
		user.setPincode(address.getPincode());
		user.setState(address.getState());
		user.setAddress(address.getFullAddress());
	}

	private void migrateLegacyAddress(User user) {
		List<UserAddress> addresses = userAddressRepository
				.findByUser_UserIdOrderByDefaultAddressDescAddressIdDesc(user.getUserId());
		if (!addresses.isEmpty() || clean(user.getHouseNo()).isBlank() || clean(user.getCity()).isBlank()
				|| clean(user.getPincode()).isBlank() || clean(user.getState()).isBlank()) {
			return;
		}

		UserAddress address = new UserAddress();
		address.setUser(user);
		address.setHouseNo(clean(user.getHouseNo()));
		address.setStreet(clean(user.getStreet()));
		address.setCity(clean(user.getCity()));
		address.setPincode(clean(user.getPincode()));
		address.setState(clean(user.getState()));
		address.setFullAddress(buildAddress(address.getHouseNo(), address.getStreet(), address.getCity(),
				address.getPincode(), address.getState()));
		address.setDefaultAddress(true);
		userAddressRepository.save(address);
	}

	private List<AddressResponse> getAddressResponses(Long userId) {
		List<AddressResponse> savedAddresses = userAddressRepository.findByUser_UserIdOrderByDefaultAddressDescAddressIdDesc(userId).stream()
				.map(this::mapAddress)
				.toList();
		List<String> savedFullAddresses = savedAddresses.stream().map(AddressResponse::getFullAddress).toList();
		List<AddressResponse> orderAddresses = orderRepository.findDistinctShippingAddressesByUserId(userId).stream()
				.filter(address -> !savedFullAddresses.contains(address))
				.map(this::mapOrderAddress)
				.toList();

		List<AddressResponse> addresses = new java.util.ArrayList<>();
		addresses.addAll(savedAddresses);
		addresses.addAll(orderAddresses);
		return addresses;
	}

	private AddressResponse mapAddress(UserAddress address) {
		return new AddressResponse(address.getAddressId(), address.getHouseNo(), address.getStreet(), address.getCity(),
				address.getPincode(), address.getState(), address.getFullAddress(), address.getDefaultAddress(), "saved");
	}

	private AddressResponse mapOrderAddress(String fullAddress) {
		ParsedAddress parsedAddress = parseAddress(fullAddress);
		return new AddressResponse(null, parsedAddress.houseNo(), parsedAddress.street(), parsedAddress.city(),
				parsedAddress.pincode(), parsedAddress.state(), fullAddress, false, "order");
	}

	private ParsedAddress parseAddress(String fullAddress) {
		String[] parts = fullAddress.split(",");
		java.util.List<String> cleanParts = java.util.Arrays.stream(parts)
				.map(String::trim)
				.filter(part -> !part.isBlank())
				.toList();

		if (cleanParts.size() >= 4) {
			return new ParsedAddress(cleanParts.get(0),
					cleanParts.size() > 4 ? String.join(", ", cleanParts.subList(1, cleanParts.size() - 3)) : "",
					cleanParts.get(cleanParts.size() - 3),
					cleanParts.get(cleanParts.size() - 2),
					cleanParts.get(cleanParts.size() - 1));
		}

		return new ParsedAddress(fullAddress, "", "", "", "");
	}

	private record ParsedAddress(String houseNo, String street, String city, String pincode, String state) {
	}
}
