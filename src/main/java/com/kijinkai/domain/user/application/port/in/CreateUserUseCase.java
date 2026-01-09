package com.kijinkai.domain.user.application.port.in;


import com.kijinkai.domain.user.domain.model.User;

public interface CreateUserUseCase {

    User createUser(String email, String password, String nickname);
}
