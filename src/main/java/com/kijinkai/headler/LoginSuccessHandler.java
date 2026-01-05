package com.kijinkai.headler;


import com.kijinkai.domain.jwt.service.JwtService;

import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import com.kijinkai.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@AllArgsConstructor
@Qualifier("LoginSuccessHandler")
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final JwtUtil jwtUtil;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //username, role
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        UUID userUuid = principal.getUserUuid();
        String role = authentication.getAuthorities().iterator().next().getAuthority();


        //JWT(Access/Refresh) 발급
        String accessToken = jwtUtil.createJWT(userUuid, role, true);
        String refreshToken = jwtUtil.createJWT(userUuid, role, false);

        // 발급한 Refresh DB 테이블에 저장
        jwtService.addRefresh(userUuid, refreshToken);

        //응답
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = String.format("{\"accessToken\":\"%s\", \"refreshToken\":\"%s\"}", accessToken, refreshToken);
        response.getWriter().write(json);
        response.getWriter().flush();
    }
}
