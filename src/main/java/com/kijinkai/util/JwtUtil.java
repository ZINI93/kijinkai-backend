package com.kijinkai.util;

import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Long accessTokenExpiresIn;
    private final Long refreshTokenExpiresIn;
    private final Long passwordResetTokenExpiresIn;

    public JwtUtil(@Value("${jwt.secret}") String secretKeyString, @Value("${jwt.access-token-expires-in}") Long accessTokenExpiresIn, @Value("${jwt.refresh-token-expires-in}") Long refreshTokenExpiresIn, @Value("${jwt.password-reset-token-expires-in}")Long passwordResetTokenExpiresIn) {
        this.secretKey = new SecretKeySpec(secretKeyString.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.accessTokenExpiresIn = accessTokenExpiresIn;
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
        this.passwordResetTokenExpiresIn = passwordResetTokenExpiresIn;
    }



    // 비밀번호 리셋 이메일 발송용 토큰 (Reset Token)
    public String createPasswordRestJWT(UUID userUuid, String role) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(userUuid.toString())
                .claim("role", role)
                .claim("type", "reset")
                .issuedAt(new Date(now))
                .expiration(new Date(now + passwordResetTokenExpiresIn))
                .signWith(secretKey)
                .compact();
    }


    // 비번 입력 폼 진입 시 발급 (Interim Token)
    public String createInterimJWT(UUID userUuid, String role){
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(userUuid.toString())
                .claim("role", role)
                .claim("type", "interim") // 중간 단계임을 명시
                .issuedAt(new Date(now))
                .expiration(new Date(now + 600000)) // 보통 5~10분으로 짧게 설정
                .signWith(secretKey)
                .compact();
    }


    public Boolean isPasswordResetValid(String token, String requiredType) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String tokenType = claims.get("type", String.class);

            return requiredType.equals(tokenType);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    //--

    // JWT 클레임 userUuid 파싱
    public UUID getUserUuid(String token) {
        String uuidStr = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
        return UUID.fromString(uuidStr);
    }

    // JWT 클레임 role 파싱
    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    //JWT 유효 여부 (위조, 시간, Access/Refresh 여부) + interim check
    public Boolean isValid(String token, Boolean isAccess) {

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String type = claims.get("type", String.class);
            if (type == null) return false;

            if (isAccess && !type.equals("access")) return false;
            if (!isAccess && !(type.equals("refresh") || type.equals("interim"))) return false;

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // JWT(access/refresh) 생성
    public String createJWT(UUID userUuid, String role, Boolean isAccess) {

        long now = System.currentTimeMillis();
        Long expiry = isAccess ? accessTokenExpiresIn : refreshTokenExpiresIn;
        String type = isAccess ? "access" : "refresh";

        return Jwts.builder()
                .subject(userUuid.toString())
                .claim("role", role)
                .claim("type", type)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiry))
                .signWith(secretKey)
                .compact();
    }


}


