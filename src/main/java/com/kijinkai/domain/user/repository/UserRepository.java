package com.kijinkai.domain.user.repository;

import com.kijinkai.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUserUuid(String userUuid);
  Optional<User> findByEmail(String email);
}