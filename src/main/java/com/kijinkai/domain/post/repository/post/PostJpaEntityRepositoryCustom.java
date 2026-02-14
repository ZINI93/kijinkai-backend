package com.kijinkai.domain.post.repository.post;

import com.kijinkai.domain.post.entity.PostJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PostJpaEntityRepositoryCustom {

    Page<PostJpaEntity> searchPost(PostCondition postCondition, Pageable pageable);
}


