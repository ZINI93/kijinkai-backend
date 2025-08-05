package com.kijinkai.domain.payment.application.port.out;

import com.kijinkai.domain.user.entity.User;

import java.util.UUID;

public interface UserPort {

    User findUserByUserUuid(UUID userUuid);
}
