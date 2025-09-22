package com.kijinkai.domain.user.adapter.in.web;

import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.user.application.dto.EmailVerificationRequestDto;
import com.kijinkai.domain.user.application.dto.UserRequestDto;
import com.kijinkai.domain.user.application.dto.UserResponseDto;
import com.kijinkai.domain.user.application.port.in.VerifyEmailUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final VerifyEmailUseCase verifyEmailUseCase;

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestBody EmailVerificationRequestDto requestDto) {
        verifyEmailUseCase.verifyEmail(requestDto);
        return ResponseEntity.ok("이메일 인증이 완료되었습니다");
    }
}
