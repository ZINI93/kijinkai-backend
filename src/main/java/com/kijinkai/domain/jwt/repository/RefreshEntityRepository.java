package com.kijinkai.domain.jwt.repository;

import com.kijinkai.domain.jwt.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RefreshEntityRepository extends JpaRepository<RefreshEntity, Long> {

  Boolean existsByRefresh(String refreshToken);

  void deleteByRefresh(String refresh);
  void deleteByUserUuid(UUID username);
  void deleteByCreatedAtBefore(LocalDateTime createdAt);

  int countByUserUuid(UUID userUuid);
  List<RefreshEntity> findUserByUserUuidOrderByCreatedAtAsc(UUID userUUid);
}