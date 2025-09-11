package com.kijinkai.domain.user.service;

import com.kijinkai.domain.user.dto.UserRequestDto;
import com.kijinkai.domain.user.dto.UserResponseDto;
import com.kijinkai.domain.user.dto.UserUpdateDto;
import com.kijinkai.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QPageRequest;

import java.util.UUID;

public interface UserService {

    UserResponseDto createUserWithValidate(UserRequestDto requestDto);
    UserResponseDto updateUserWithValidate(UUID userUuid, UserUpdateDto updateDto);
    UserResponseDto updateUserPassword(UUID userUuid, UserUpdateDto updateDto);

    void deleteUser(UUID userUuid);
    User findUserByUserUuid(UUID userUuid);

    UserResponseDto getUserInfo(UUID userUuid);
    Page<UserResponseDto> findAllByUsers(UUID userUuid, String email, String nickname, Pageable pageable);
}
