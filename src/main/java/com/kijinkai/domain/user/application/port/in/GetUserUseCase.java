package com.kijinkai.domain.user.application.port.in;

import com.kijinkai.domain.user.application.dto.request.UserRequestDto;
import com.kijinkai.domain.user.application.dto.response.UserEditInfoResponse;
import com.kijinkai.domain.user.application.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

public interface GetUserUseCase {

    UserResponseDto getUserInfo(UUID userUuid);

    Page<UserResponseDto> findAllByEmailAndNickName(UUID userUuid, String email, String nickName, Pageable pageable);

    Boolean existsByUser(UserRequestDto userRequestDto);

    UserEditInfoResponse userEditInfo(UUID userUuid);
}
