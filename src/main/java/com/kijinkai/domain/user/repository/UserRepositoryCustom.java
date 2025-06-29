package com.kijinkai.domain.user.repository;

import com.kijinkai.domain.user.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

public interface UserRepositoryCustom {
    Page<UserResponseDto> findByNameAndNickname(String email, String nickname, Pageable pageable);
}
