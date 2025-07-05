package com.kijinkai.domain.user.entity;


import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.user.dto.UserUpdateDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    //-- 추가

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "email_verifiedAt", nullable = false)
    private LocalDateTime emailVerifiedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus userStatus = UserStatus.PENDING;

    @Builder
    public User(UUID userUuid, String email, String password, String nickname, UserRole userRole, Boolean emailVerified, UserStatus userStatus) {
        this.userUuid = userUuid != null ? userUuid : UUID.randomUUID();
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.userRole = userRole != null ? userRole : UserRole.USER;
        this.userStatus = userStatus != null ? userStatus : UserStatus.PENDING;
        this.emailVerified = emailVerified != null ? emailVerified : false;
    }

    public void updateUser(String password, UserUpdateDto updateDto) {
        this.password = password;
        this.nickname = updateDto.getNickname();
    }

    /*
    * 이메일 인증 처리 메소드
     */
    public void verifyEmail(){
        this.emailVerified = true;
        this.emailVerifiedAt = LocalDateTime.now();
        this.userStatus = UserStatus.ACTIVE;
    }

    /*
    이메일 인증 여부 확인
     */
    public Boolean isEmailVerified(){
        return this.emailVerified;
    }

    /**
     * 계정 확성화 여부 확인
     */

    public boolean isActive(){
        return this.userStatus == UserStatus.ACTIVE;
    }




}
