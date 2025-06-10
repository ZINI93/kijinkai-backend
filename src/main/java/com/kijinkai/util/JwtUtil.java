package com.kijinkai.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey SECRET_KEY;

    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    //create token
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((System.currentTimeMillis() + 1000 * 60 * 60 * 10))) //유효기간 10시간
                .signWith(SECRET_KEY)
                .compact();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }


    public boolean validateToken(String token, String email) {
        final String extractEmail = extractEmail(token);
        return (email.equals(extractEmail)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

}
