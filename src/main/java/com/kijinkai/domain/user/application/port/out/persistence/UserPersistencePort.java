package com.kijinkai.domain.user.application.port.out.persistence;

import com.kijinkai.domain.user.application.dto.UserResponseDto;
import com.kijinkai.domain.user.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;


public interface UserPersistencePort{

    User saveUser(User user);

    Boolean existsByEmail(String email);

    Optional<User> findByUserUuid(UUID uuid);
    Optional<User> findByEmail(String email);
    Page<User> findAllByEmailAndName(String email, String name, Pageable pageable);

    void deleteUser(User user);
}
