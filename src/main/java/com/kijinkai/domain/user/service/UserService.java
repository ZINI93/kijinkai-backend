package com.kijinkai.domain.user.service;

import com.kijinkai.domain.user.dto.UserRequestDto;
import com.kijinkai.domain.user.dto.UserResponseDto;
import com.kijinkai.domain.user.dto.UserUpdateDto;

public interface UserService {

    UserResponseDto createUserWithValidate(UserRequestDto requestDto);
    UserResponseDto updateUserWithValidate(String userUuid, UserUpdateDto updateDto);
    void deleteUser(String userUuid);

    UserResponseDto getUserInfo(String userUuid);
}
