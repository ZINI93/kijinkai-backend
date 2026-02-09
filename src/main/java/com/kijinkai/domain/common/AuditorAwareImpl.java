package com.kijinkai.domain.common;


import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 1. 인증 정보가 없거나 인증되지 않은 경우 처리
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        // 2. 익명 사용자(anonymousUser)인 경우 처리
        if (principal instanceof String && "anonymousUser".equals(principal)) {
            return Optional.empty();
        }

        if (principal instanceof CustomUserDetails userDetails) {
            UUID userUuid = userDetails.getUserUuid();
            if (userUuid != null) {
                String loginId = userUuid.toString();
                return Optional.of(loginId);
            }
        }

        // 4. 그 외의 경우 기본 Name 반환 (fallback)
        return Optional.ofNullable(authentication.getName());
    }
}