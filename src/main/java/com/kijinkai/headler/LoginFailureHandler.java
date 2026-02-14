package com.kijinkai.headler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
// 1. 응답 설정 (401 Unauthorized 및 UTF-8 설정)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        // 2. 메시지 결정
        String errorMessage = "로그인 정보가 올바르지 않습니다.";

        if (exception instanceof BadCredentialsException) {
            // 비밀번호가 틀린 경우 (현재 로그 상황)
            errorMessage = "비밀번호가 일치하지 않습니다.";
        } else if (exception instanceof InternalAuthenticationServiceException) {
            // 존재하지 않는 아이디거나 시스템 내부 오류
            errorMessage = "계정 정보가 존재하지 않습니다.";
        }

        // 3. JSON 응답 전송
        String jsonResponse = String.format("{\"message\": \"%s\"}", errorMessage);
        response.getWriter().write(jsonResponse);

        // 확인용 로그 (나중에 삭제하셔도 됩니다)
        System.out.println("클라이언트로 전송된 에러 메시지: " + errorMessage);
    }
}
