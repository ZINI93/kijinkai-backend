package com.kijinkai.domain.jwt.controller;

import com.kijinkai.domain.jwt.dto.JwtResponseDto;
import com.kijinkai.domain.jwt.dto.RefreshRequestDto;
import com.kijinkai.domain.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtController {


    private final JwtService jwtService;

    public JwtController(JwtService jwtService) {
        this.jwtService = jwtService;
    }


    // 소셜 로그인 쿠키 방식의 Refresh 토큰 헤더 방식으로 교환
    @PostMapping(value = "/jwt/exchange", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JwtResponseDto jwtExchangeApi(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return jwtService.cookie2Header(request, response);
    }


    //Refresh 토큰으로 Access 토큰 재발급
    @PostMapping(value = "/jwt/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JwtResponseDto jwtRefreshApi(
            @Validated @RequestBody RefreshRequestDto dto
    ) {
        return jwtService.refreshRotate(dto);

    }
}
