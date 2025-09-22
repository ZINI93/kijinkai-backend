package com.kijinkai.domain.user.application.port.in;


import com.kijinkai.domain.user.application.dto.UserRequestDto;
import com.kijinkai.domain.user.application.dto.UserResponseDto;

public interface CreateUserUseCase {

    UserResponseDto createUser(UserRequestDto requestDto);
}
