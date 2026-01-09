package com.kijinkai.domain.jwt.service;

import com.kijinkai.domain.jwt.dto.JwtResponseDto;
import com.kijinkai.domain.jwt.dto.RefreshRequestDto;
import com.kijinkai.domain.jwt.entity.RefreshEntity;
import com.kijinkai.domain.jwt.repository.RefreshEntityRepository;
import com.kijinkai.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class JwtService {

    private final RefreshEntityRepository refreshRepository;
    private final JwtUtil jwtUtil;



    // 소셜 로그인 성공 후 쿠키(Refresh) -> 헤더 방식으로 응답
    @Transactional
    public JwtResponseDto cookie2Header(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        //cookie list
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new RuntimeException("쿠키가 존재하지 않습니다.");
        }

        //Refresh Token 획득
        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
                break;
            }
        }

        if (refreshToken == null) {
            throw new RuntimeException("RefreshToken 이 존재하지 않습니다.");
        }

        //RefreshToken 검증
        Boolean isValid = jwtUtil.isValid(refreshToken, false);
        if (!isValid) {
            throw new RuntimeException("유효하지 않는 RefreshToken 입니다.");
        }

        //정보 추출
        UUID userUuid = jwtUtil.getUserUuid(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        //Token 생성
        String newAccessToken = jwtUtil.createJWT(userUuid, role, true);
        String newRefreshToken = jwtUtil.createJWT(userUuid, role, false);


        //기존 RefreshToken DB 삭제 후 신규 추가
        RefreshEntity newRefreshEntity = RefreshEntity.builder()
                .userUuid(userUuid)
                .refresh(newRefreshToken)
                .build();

        removeRefresh(refreshToken);
        refreshRepository.flush();
        refreshRepository.save(newRefreshEntity);


        //기존 쿠키 제거
        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        return new JwtResponseDto(newAccessToken, newRefreshToken);


    }


    // Refresh 토큰으로 Access 토큰 재발급 로직 (Rotate 포함)
    @Transactional
    public JwtResponseDto refreshRotate(RefreshRequestDto dto) {

        String refreshToken = dto.getRefreshToken();

        //RefreshToken 검증
        Boolean isValid = jwtUtil.isValid(refreshToken, false);
        if (!isValid) {
            throw new RuntimeException("유효하지 않는 RefreshToken 입니다.");
        }

        // DB에 존재하는 RefreshToken 만 재발급
        if (!refreshRepository.existsByRefresh(refreshToken)) {
            throw new RuntimeException("DB에 존재하지 않는 RefreshToken 입니다.");
        }

        //정보 추출
        UUID userUuid = jwtUtil.getUserUuid(refreshToken);
        String role = jwtUtil.getRole(refreshToken);


        //토큰 생성
        String newAccessToken = jwtUtil.createJWT(userUuid, role, true);
        String newRefreshToken = jwtUtil.createJWT(userUuid, role, false);


        //기존 RefreshToken DB 삭제 후 신규 추가
        RefreshEntity refreshTokenEntity = RefreshEntity.builder()
                .userUuid(userUuid)
                .refresh(newRefreshToken)
                .build();


        removeRefresh(refreshToken);
        refreshRepository.save(refreshTokenEntity);

        return new JwtResponseDto(newAccessToken, newRefreshToken);
    }


    // JWT Refresh 토큰 발급 후 저장 메소드
    @Transactional
    public void addRefresh(UUID userUuid, String refreshToken) {

        // 이미 같은 refresh 가 DB에 있으면 저장 스킵
        if (refreshRepository.existsByRefresh(refreshToken)) {
            return;
        }

        int count = refreshRepository.countByUserUuid(userUuid);

        // 과거 발급된 Refresh 토큰을 삭제해서 계정당 3개를 유지
        if (count >= 3) {
            List<RefreshEntity> olds = refreshRepository.findUserByUserUuidOrderByCreatedAtAsc(userUuid);
            int deleteCount = (count - 2);
            for (int i = 0; i < deleteCount; i++) {
                refreshRepository.delete(olds.get(i));
            }
        }


        RefreshEntity refreshEntity = RefreshEntity.builder()
                .userUuid(userUuid)
                .refresh(refreshToken)
                .build();

        refreshRepository.save(refreshEntity);
    }





    // JWT Refresh 존재 확인 메소드
    public Boolean existsRefresh(String refreshToken) {
        return refreshRepository.existsByRefresh(refreshToken);
    }


    // JWT Refresh 토큰 삭제 메소드
    @Transactional
    public void removeRefresh(String refreshToken) {
        refreshRepository.deleteByRefresh(refreshToken);
    }


    // 특정 유저 Refresh 토큰 모두 삭제 (탈퇴)
    @Transactional
    public void removeRefreshUser(UUID userUuid) {
        refreshRepository.deleteByUserUuid(userUuid);
    }
}
