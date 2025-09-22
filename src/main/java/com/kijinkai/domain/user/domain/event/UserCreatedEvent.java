package com.kijinkai.domain.user.domain.event;

import com.kijinkai.domain.user.domain.model.User;

import java.util.UUID;

public record UserCreatedEvent(
        UUID userUuid,
        String email,
        String nickname
) {
    public static UserCreatedEvent from(User user){

        return new UserCreatedEvent(
                user.getUserUuid(),
                user.getEmail(),
                user.getNickname()
        );
    }
}
