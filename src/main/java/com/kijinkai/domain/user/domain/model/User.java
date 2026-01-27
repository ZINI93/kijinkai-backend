package com.kijinkai.domain.user.domain.model;

import com.kijinkai.domain.user.domain.exception.InvalidUserStatusException;
import com.kijinkai.domain.user.domain.exception.UserRoleValidateException;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    private Long userId;
    private UUID userUuid;
    private String email;
    private String password;
    private String nickname;
    private UserRole userRole;
    private boolean emailVerified;
    private LocalDateTime emailVerifiedAt;
    private UserStatus userStatus;
    private LocalDateTime createdAt;

    //Social 추가
    private Boolean isSocial;
    private SocialProviderType socialProviderType;

    /**
     * 이메일 인증 처리
     */
    public void verifyEmail() {
        this.emailVerified = true;
        this.emailVerifiedAt = LocalDateTime.now();
    }

    /**
     * 이메일 인증 여부 확인  - login check
     */

    public Boolean isEmailVerified() {
        return this.emailVerified;
    }

    /**
     * 계정 활성화
     */
    public boolean isActive() {
        return this.userStatus == UserStatus.ACTIVE;
    }

    /**
     * 게정 활성화 체크
     */
    public void validateActive() {
        if (this.userStatus != UserStatus.ACTIVE) {
            throw new InvalidUserStatusException("User is not eligible for customer registration");
        }
    }

    /**
     * 유저 Role 업데이트
     */
    public void updateRole(UserRole userRole) {
        this.userRole = userRole;
    }

    /**
     * 유저 프로필 업데이트
     *
     * @param nickname
     * @param encodedPassword
     */
    public void updateUser(String nickname, String encodedPassword) {
        this.nickname = (nickname != null) ? nickname : this.nickname;
        this.password = (encodedPassword != null) ? encodedPassword : this.password;
    }

    public void oAuth2LoginUpdate(String nickname) {
        this.nickname = nickname;
    }


    public void updatePassword(String password) {
        this.password = password;
    }


    /**
     * 관리자 역할 검증
     */
    public void validateAdminRole() {
        if (this.userRole != UserRole.ADMIN) {
            throw new UserRoleValidateException(
                    String.format("Admin role required. Current role: %s", this.userRole)
            );
        }
    }

}
