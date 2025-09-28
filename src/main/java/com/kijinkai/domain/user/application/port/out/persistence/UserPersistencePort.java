package com.kijinkai.domain.user.application.port.out.persistence;

import com.kijinkai.domain.user.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;


public interface UserPersistencePort{

    User saveUser(User user);

    Boolean existsByEmail(String email);

    Optional<User> findByUserUuid(UUID uuid);
    Optional<User> findByEmail(String email);
    Page<User> findAllByEmailAndNickName(String email, String nickname, Pageable pageable);

    void deleteUser(User user);
}
