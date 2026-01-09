package com.kijinkai.domain.user.application.port.in;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

public interface DeleteUserUseCase {

    void deleteUser(UUID userUuid) throws AccessDeniedException;




}
