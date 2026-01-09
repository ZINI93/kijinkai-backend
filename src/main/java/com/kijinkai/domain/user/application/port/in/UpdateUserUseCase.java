package com.kijinkai.domain.user.application.port.in;

import com.kijinkai.domain.user.application.dto.response.UserResponseDto;
import com.kijinkai.domain.user.application.dto.request.UserUpdateDto;

import java.util.UUID;

public interface UpdateUserUseCase {

    UserResponseDto updateUserProfile(UUID userUuid, UserUpdateDto updateDto);
    UserResponseDto updateUserPassword(UUID userUuid, UserUpdateDto updateDto);

}



