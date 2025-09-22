package com.kijinkai.domain.user.application.port.in;

import com.kijinkai.domain.user.domain.model.User;

import java.util.UUID;

public interface DeleteUserUseCase {

    void deleteUser(UUID userUUid);




}
