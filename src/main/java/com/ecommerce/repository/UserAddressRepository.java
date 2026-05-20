package com.ecommerce.repository;

import com.ecommerce.entity.UserAddress;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
	List<UserAddress> findByUser_UserIdOrderByDefaultAddressDescAddressIdDesc(Long userId);

	Optional<UserAddress> findByAddressIdAndUser_UserId(Long addressId, Long userId);
}
