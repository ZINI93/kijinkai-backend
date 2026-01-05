package com.kijinkai.domain.user.application.port.in;

import com.kijinkai.domain.user.application.dto.UserRequestDto;
import com.kijinkai.domain.user.domain.model.User;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

public interface DeleteUserUseCase {

    void deleteUser(UUID userUuid) throws AccessDeniedException;




}
