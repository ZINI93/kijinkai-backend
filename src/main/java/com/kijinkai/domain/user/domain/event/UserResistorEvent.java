package com.kijinkai.domain.user.domain.event;


import com.kijinkai.domain.user.domain.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserResistorEvent{

    private final User user;
}
