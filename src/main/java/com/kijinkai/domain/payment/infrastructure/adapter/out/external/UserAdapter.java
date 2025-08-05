package com.kijinkai.domain.payment.infrastructure.adapter.out.external;

import com.kijinkai.domain.payment.application.port.out.UserPort;
import com.kijinkai.domain.user.entity.User;
import com.kijinkai.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class UserAdapter implements UserPort {

    private final UserService userService;

    @Override
    public User findUserByUserUuid(UUID userUuid) {
        return userService.findUserByUserUuid(userUuid);
    }
}
