//package com.kijinkai.domain.user.adapter.in.web;
//
//import com.kijinkai.domain.user.application.dto.LoginRequest;
//import com.kijinkai.util.JwtUtil;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//
//@Slf4j
//@RestController
//@RequiredArgsConstructor
//public class AuthController {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @PostMapping("/authenticate")
//    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
//        try {
//            // 사용자 인증
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            loginRequest.getEmail(),
//                            loginRequest.getPassword()
//                    )
//            );
//
//            // 실제 JWT 토큰 생성
//            String jwt = jwtUtil.generateToken(loginRequest.getEmail());
//
//            return ResponseEntity.ok(Map.of(
//                    "token", jwt,
//                    "user", Map.of(
//                            "email", loginRequest.getEmail(),
//                            "nickname", loginRequest.getEmail().split("@")[0]
//                    )
//            ));
//
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Map.of(
//                    "message", "이메일 또는 비밀번호가 잘못되었습니다."
//            ));
//        }
//    }
//}
