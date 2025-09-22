package com.kijinkai.domain.user.application.port.in;

import com.kijinkai.domain.user.application.dto.UserResponseDto;
import com.kijinkai.domain.user.application.dto.UserUpdateDto;

import java.util.UUID;

public interface UpdateUserUseCase {

    UserResponseDto updateUserProfile(UUID userUuid, UserUpdateDto updateDto);
    UserResponseDto updateUserPassword(UUID userUuid, UserUpdateDto updateDto);
}
