package com.kijinkai.domain.user.entity;

public enum UserStatus {
    PENDING,    // 이메일 인증 대기
    ACTIVE,     // 활성 사용자
    SUSPENDED,  // 정지된 사용자
    DELETED     // 탈퇴한 사용자
}
