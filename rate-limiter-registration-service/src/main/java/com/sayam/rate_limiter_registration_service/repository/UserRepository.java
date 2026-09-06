package com.sayam.rate_limiter_registration_service.repository;

import com.sayam.rate_limiter_registration_service.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByIpAddress(String ipAddress);
}
