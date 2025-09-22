package com.kijinkai.domain.user.adapter.out.persistence.repository;

import com.kijinkai.domain.user.adapter.out.persistence.entity.UserJpaEntity;
import com.kijinkai.domain.user.application.dto.UserResponseDto;
import com.kijinkai.domain.user.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    Page<UserJpaEntity> findAllByEmailAndName(String email, String name, Pageable pageable);
}
