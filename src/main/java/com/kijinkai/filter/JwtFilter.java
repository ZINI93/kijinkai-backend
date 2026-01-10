package com.kijinkai.filter;

import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import com.kijinkai.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = authorization.substring(7).trim();
        if (accessToken.isEmpty()){
            filterChain.doFilter(request, response);
            return;
        }

        boolean isResetPassword = request.getRequestURI().equals("/api/auth/reset-password");

        boolean checkMode = !isResetPassword;

        if (jwtUtil.isValid(accessToken, checkMode)) {

            UUID userUuid = jwtUtil.getUserUuid(accessToken);
            String role = jwtUtil.getRole(accessToken);

            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

            AuthPrincipal authPrincipal = new AuthPrincipal(userUuid);

            CustomUserDetails userDetails = new CustomUserDetails(
                    authPrincipal,
                    "",
                    authorities,
                    true
            );

            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"토큰 만료 또는 유효하지 않은 토큰\"}");
            return;
        }
    }
}
