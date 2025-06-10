package com.kijinkai.domain.platform.repository;

import com.kijinkai.domain.platform.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlatformRepository extends JpaRepository<Platform, Long> {

    Optional<Platform> findByUserUserUuidAndPlatformUuid(String userUuid, String platformUuid);
    Optional<Platform> findByPlatformUuid(String platformUuid);
}