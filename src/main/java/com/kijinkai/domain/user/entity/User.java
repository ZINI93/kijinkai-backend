package com.kijinkai.domain.user.entity;


import com.kijinkai.domain.BaseEntity;
import com.kijinkai.domain.user.dto.UserUpdateDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Entity
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long userId;

    @Column(name = "user_uuid", unique = true, nullable = false, updatable = false)
    private UUID userUuid;

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
    public User(UUID userUuid, String email, String password, String nickname, UserRole userRole) {
        this.userUuid = userUuid != null ? userUuid : UUID.randomUUID();
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.userRole = userRole != null ? userRole : UserRole.USER;
    }

    public void updateUser(String password, UserUpdateDto updateDto) {
        this.password = password;
        this.nickname = updateDto.getNickname();
    }
}
