package com.kijinkai.domain.user.adapter.out.persistence.repository;

import com.kijinkai.domain.user.adapter.out.persistence.entity.UserJpaEntity;
import com.kijinkai.domain.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserJpaEntity, Long>, UserRepositoryCustom {

  Optional<UserJpaEntity> findByUserUuid(UUID userUuid);
  Optional<UserJpaEntity> findByEmail(String email);

  Boolean existsByEmail(String email);
}