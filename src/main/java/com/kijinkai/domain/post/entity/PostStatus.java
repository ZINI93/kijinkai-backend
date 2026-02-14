package com.kijinkai.domain.post.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostStatus {
    DRAFT("임시 저장"),      // 작성 중이며 사용자에게 노출되지 않음
    PUBLISHED("발행됨"),    // 정상적으로 공개된 상태
    HIDDEN("숨김"),        // 관리자나 작성자에 의해 일시적으로 숨겨진 상태
    REPORTED("신고됨"),     // 신고 접수로 인해 검토가 필요한 상태
    DELETED("삭제됨");      // 논리 삭제(Soft Delete) 상태

    private final String description;
}
