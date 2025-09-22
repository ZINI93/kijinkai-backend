package com.kijinkai.domain.payment.infrastructure.adapter.out.external;

import com.kijinkai.domain.payment.application.port.out.UserPort;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import com.kijinkai.domain.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class UserAdapter implements UserPort {

    private final UserPersistencePort userPersistencePort;

    @Override
    public User findUserByUserUuid(UUID userUuid) {
        return userPersistencePort.findByUserUuid(userUuid)
                .orElseThrow( () -> new UserNotFoundException(String.format("User not found for user uuid: %s", userUuid)));
    }
}
