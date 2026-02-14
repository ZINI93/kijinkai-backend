package com.kijinkai.domain.post.repository.postimage;

import com.kijinkai.domain.post.entity.PostImageJpaEntity;
import com.kijinkai.domain.post.entity.PostJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostImageJpaEntityRepository extends JpaRepository<PostImageJpaEntity, Long> {

    Optional<PostImageJpaEntity> findByPostPostId(Long postId);
    List<PostImageJpaEntity> findByPostPostIdIn(List<Long> postId);
}