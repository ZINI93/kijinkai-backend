package com.kijinkai.domain.user.adapter.out.persistence.repository;

import com.kijinkai.domain.user.adapter.out.persistence.entity.UserJpaEntity;
import com.kijinkai.domain.user.domain.model.SocialProviderType;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.user.domain.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserJpaEntity, Long>, UserRepositoryCustom {

  Optional<UserJpaEntity> findByUserUuid(UUID userUuid);
  Optional<UserJpaEntity> findByEmail(String email);
  Optional<UserJpaEntity> findByEmailAndUserStatusAndIsSocial(String email, UserStatus userStatus, Boolean isSocial);
  Optional<UserJpaEntity> findByEmailAndIsSocial(String email, Boolean isSocial);
  Optional<UserJpaEntity> findByEmailAndUserStatus(String email, UserStatus userStatus);
  Optional<UserJpaEntity> findByNickname(String nickname);

  List<UserJpaEntity> findAllByUserUuidIn(List<UUID> userUuid);
  Boolean existsByEmail(String email);

  void deleteByEmail(String email);
}