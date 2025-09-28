package com.kijinkai.domain.user.adapter.out.persistence.repository;

import com.kijinkai.domain.user.adapter.out.persistence.entity.UserJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    Page<UserJpaEntity> findAllByEmailAndNickName(String email, String name, Pageable pageable);
}
