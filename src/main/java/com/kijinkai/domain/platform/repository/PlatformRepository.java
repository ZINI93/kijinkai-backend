package com.kijinkai.domain.platform.repository;

import com.kijinkai.domain.platform.entity.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {

    Optional<Platform> findByUserUserUuidAndPlatformUuid(UUID userUuid, UUID platformUuid);
    Optional<Platform> findByPlatformUuid(UUID platformUuid);
    Page<Platform> findAllByPlatform(Pageable pageable);
}