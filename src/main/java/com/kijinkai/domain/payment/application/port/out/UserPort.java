package com.kijinkai.domain.payment.application.port.out;

import com.kijinkai.domain.user.domain.model.User;

import java.util.UUID;

public interface UserPort {

    User findUserByUserUuid(UUID userUuid);
}
