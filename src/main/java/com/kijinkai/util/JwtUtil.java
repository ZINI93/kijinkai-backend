package com.kijinkai.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Long accessTokenExpiresIn;
    private final Long refreshTokenExpiresIn;

    public JwtUtil(@Value("${jwt.secret}") String secretKeyString, @Value("${jwt.access-token-expires-in}") Long accessTokenExpiresIn, @Value("${jwt.refresh-token-expires-in}") Long refreshTokenExpiresIn) {
        this.secretKey = new SecretKeySpec(secretKeyString.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.accessTokenExpiresIn = accessTokenExpiresIn;
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
    }

    // JWT 클레임 username 파싱
//    public String getUsername(String token) {
//        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("sub", String.class);
//    }

    // JWT 클레임 userUuid 파싱
    public UUID getUserUuid(String token) {
        String uuidStr = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();

        return UUID.fromString(uuidStr);
    }

    // JWT 클레임 role 파싱
    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }


    //JWT 유효 여부 (위조, 시간, Access/Refresh 여부)
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
            if (!isAccess && !type.equals("refresh")) return false;

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


