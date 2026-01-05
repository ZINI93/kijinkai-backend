package com.kijinkai.headler;

import com.kijinkai.domain.jwt.service.JwtService;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import com.kijinkai.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Qualifier("SocialSuccessHandler")
public class SocialSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final String allowedOrigins;
    private final JwtUtil jwtUtil;

    public SocialSuccessHandler(JwtService jwtService, @Value("${cors.allowed-origins}") String allowedOrigins, JwtUtil jwtUtil) {
        this.jwtService = jwtService;
        this.allowedOrigins = allowedOrigins;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // username, role

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        UUID userUuid = principal.getUserUuid();

        String role = authentication.getAuthorities().iterator().next().getAuthority();

        // JWT(Refresh)발급
        String refreshToken = jwtUtil.createJWT(userUuid, "ROLE_ " + role, false);

        // 발급한 Refresh DB 테이블에 저장 (Refresh Whitelist)
        jwtService.addRefresh(userUuid, refreshToken);


        //응답
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(10); // 10초 (프론트에서 발급 후 바로 헤더 전환 로직 진행 예정)

        response.addCookie(refreshCookie);
        response.sendRedirect(allowedOrigins + "/cookie");
    }
}
