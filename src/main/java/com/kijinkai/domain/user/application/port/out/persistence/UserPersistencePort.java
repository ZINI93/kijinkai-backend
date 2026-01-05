package com.kijinkai.domain.user.application.port.out.persistence;

import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.user.domain.model.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;


public interface UserPersistencePort{


    //저장
    User saveUser(User user);

    //검증
    Boolean existsByEmail(String email);

    //조회
    Optional<User> findByUserUuid(UUID uuid);
    Optional<User> findByEmailAndIsSocial(String email, Boolean isSocial);
    Optional<User> findByEmailAndUserStatusAndIsSocial(String email, UserStatus userStatus, Boolean isSocial);
    Optional<User> findByEmailAndUserStatus(String email, UserStatus userStatus) ;

    Page<User> findAllByEmailAndNickName(String email, String nickname, Pageable pageable);



    //삭제
    void deleteUser(User user);
    void deleteByEmail(String email);
}
