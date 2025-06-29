package com.kijinkai.domain.platform.repository;

import com.kijinkai.domain.platform.entity.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.LongStream;


@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {

    Optional<Platform> findByUserUserUuidAndPlatformUuid(UUID userUuid, UUID platformUuid);
    Optional<Platform> findByPlatformUuid(UUID platformUuid);
}