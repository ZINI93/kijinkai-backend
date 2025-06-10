package com.kijinkai.domain.user.entity;


import com.kijinkai.domain.BaseEntity;
import com.kijinkai.domain.user.dto.UserUpdateDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Table(name = "users")
@Entity
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long userId;

    @Column(name = "user_uuid", unique = true, nullable = false, updatable = false)
    private String userUuid;

    @Column(name = "email", unique = true, nullable = false, updatable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nick_name", nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole userRole;

    @Builder
    public User(String userUuid, String email, String password, String nickname, UserRole userRole) {
        this.userUuid = userUuid != null ? userUuid : UUID.randomUUID().toString();
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.userRole = userRole != null ? userRole : UserRole.USER;
    }

    public void updateUser(String password, String nickname) {
        this.password = password;
        this.nickname = nickname;
    }
}
