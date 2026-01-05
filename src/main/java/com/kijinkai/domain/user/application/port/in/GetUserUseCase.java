package com.kijinkai.domain.user.application.port.in;

import com.kijinkai.domain.user.application.dto.UserRequestDto;
import com.kijinkai.domain.user.application.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

public interface GetUserUseCase {

    UserResponseDto getUserInfo(UUID userUuid) throws AccessDeniedException;
    Page<UserResponseDto> findAllByEmailAndNickName(UUID userUuid, String email, String nickName, Pageable pageable);
    Boolean existsByUser(UserRequestDto userRequestDto);
}
