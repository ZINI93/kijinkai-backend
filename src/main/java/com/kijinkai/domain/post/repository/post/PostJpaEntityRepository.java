package com.kijinkai.domain.post.repository.post;

import com.kijinkai.domain.post.entity.PostJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PostJpaEntityRepository extends JpaRepository<PostJpaEntity, Long> , PostJpaEntityRepositoryCustom{

    Optional<PostJpaEntity> findByPostUuid(UUID postUuid);
    Optional<PostJpaEntity> findByAuthorUuidAndPostUuid(UUID authorUserUuid, UUID postUuid);
}