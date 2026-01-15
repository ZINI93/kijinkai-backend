package com.kijinkai.config;


import com.kijinkai.domain.jwt.service.JwtService;
import com.kijinkai.domain.user.domain.model.UserRole;
import com.kijinkai.filter.JwtFilter;
import com.kijinkai.filter.LoginFilter;
import com.kijinkai.headler.RefreshTokenLogoutHandler;
import com.kijinkai.headler.SocialSuccessHandler;
import com.kijinkai.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@EnableMethodSecurity
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final AuthenticationSuccessHandler loginSuccessHandler;
    private final SocialSuccessHandler socialSuccessHandler;
    private final String allowedOrigins;
    private final JwtService jwtService;
    private final JwtUtil jwtUtil;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration,
                          @Qualifier("loginSuccessHandler") AuthenticationSuccessHandler loginSuccessHandler, SocialSuccessHandler socialSuccessHandler, @Value("${cors.allowed-origins}") String allowedOrigins, JwtService jwtService, JwtUtil jwtUtil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.loginSuccessHandler = loginSuccessHandler;
        this.socialSuccessHandler = socialSuccessHandler;
        this.allowedOrigins = allowedOrigins;
        this.jwtService = jwtService;
        this.jwtUtil = jwtUtil;
    }


    // 커스텀 자체 로그인 필터를 위한 AuthenticationManager Been 으로 수동 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Password 단방향(BCrypt) 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    //권한 계층
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withRolePrefix("ROLE_")
                .role(UserRole.ADMIN.toString()).implies(UserRole.USER.name())
                .build();
    }

    // CORS Bean - 수정필요
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins)); // Vite 포트
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization", "Set-Cookie"));
        configuration.setMaxAge(3600L);


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    // SecurityFilterChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // CSRF 보안 필터
        http
                .csrf(AbstractHttpConfigurer::disable);

        // CORS 설정
        http
                .cors(core -> core.configurationSource(corsConfigurationSource()));


        // From 기반 인증 필터들 비활성화
        http
                .formLogin(AbstractHttpConfigurer::disable);


        // 기본 Basic 인증 필터 비활성화
        http
                .httpBasic(AbstractHttpConfigurer::disable);


        // 기본 로그아웃 필터
        http
                .logout(logout -> logout
                        .addLogoutHandler(new RefreshTokenLogoutHandler(jwtService, jwtUtil)));


        // 인가
        http
                        .authorizeHttpRequests((auth) -> auth
                                .requestMatchers("/jwt/exchange", "/jwt/refresh").permitAll()
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                                .requestMatchers(HttpMethod.POST, "/users/sign-up", "/users/exists").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/auth/forget-password", "/api/auth/issuance-password-token").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/auth/reset-password").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/v1/main-page").authenticated()
                                .requestMatchers(HttpMethod.GET, "/exchange-rate/**").permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/order-items/**").authenticated()


                                .requestMatchers(HttpMethod.POST,"/api/v1/order-items/add").authenticated()
                                .requestMatchers("/api/v1/orders/**").hasRole(UserRole.USER.name())
                                .requestMatchers("/api/v1/customers/**").hasRole(UserRole.ADMIN.name()) // 임시로 모든 사용자 허용


                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                .requestMatchers("/api/**").hasRole(UserRole.USER.name())
                                .requestMatchers("/api/admin/**").hasRole(UserRole.ADMIN.name())

                                //관리자
                                .requestMatchers(HttpMethod.POST, "/api/v1/admin/order-items/**").hasRole(UserRole.ADMIN.name())


                                .anyRequest().authenticated()

                );

        //예외 처리
        http
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // 401 응답
                        })
                        .accessDeniedHandler(((request, response, accessDeniedException) -> {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN); //403 응답
                        }))


                );

        http
                .addFilterBefore(new JwtFilter(jwtUtil), LogoutFilter.class);

        // oauth2 인증
        http
                .oauth2Login(oauth2 ->
                        oauth2.successHandler(socialSuccessHandler));

        // JWT 필터 추가 (기존 필터 앞에 배치)
        http
                .addFilterBefore(new LoginFilter(authenticationManager(authenticationConfiguration), loginSuccessHandler), UsernamePasswordAuthenticationFilter.class);


        // 세션 필터 설정 (stateless)
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );


        return http.build();
    }
}


