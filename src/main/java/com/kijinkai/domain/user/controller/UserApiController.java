package com.kijinkai.domain.user.controller;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.user.dto.UserRequestDto;
import com.kijinkai.domain.user.dto.UserResponseDto;
import com.kijinkai.domain.user.dto.UserUpdateDto;
import com.kijinkai.domain.user.service.CustomUserDetails;
import com.kijinkai.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "Users management API")
@SecurityRequirement(name = "BearerAuth")
public class UserApiController {

    private UUID extractUserUuid(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return getUuid(customUserDetails);
    }

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "user register", description = "Receive and save user email, password, nickname")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successful membership registration"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Email already exists"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public ResponseEntity<BasicResponseDto<UserResponseDto>> createUser(@Valid @RequestBody UserRequestDto requestDto){

        log.info("사용자 회원가입 요청 - 이메일: {}", requestDto.getEmail());

        UserResponseDto user = userService.createUserWithValidate(requestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{userUuid}")
                .buildAndExpand(user.getUserUuid())
                .toUri();
        return ResponseEntity.created(location).body(BasicResponseDto.success("Successful join", user));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "User info Inquiry", description = "User info Inquiry")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful user info Inquiry"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Failed user info Inquiry"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public ResponseEntity<BasicResponseDto<UserResponseDto>> getUserInfo(Authentication authentication){
        UUID userUuid = extractUserUuid(authentication);
        log.info("사용자 조회 요청 - 사용자 UUID: {}", userUuid);

        UserResponseDto user = userService.getUserInfo(userUuid);
        log.info("사용자 조회 완료 - 고객 UUID: {}", user.getUserUuid());

        return ResponseEntity.ok(BasicResponseDto.success("사용자 정보를 성공적으로 응답했습니다.",user));
    }

    private static UUID getUuid(CustomUserDetails customUserDetails) {
        return customUserDetails.getUserUuid();
    }


    @PutMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update user", description = "Update user password, nickname")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful user info update"),
            @ApiResponse(responseCode = "404", description = "Failed user info update"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public ResponseEntity<BasicResponseDto<UserResponseDto>> editUser(@Valid @RequestBody UserUpdateDto updateDto,
                                                        Authentication authentication){
        UUID userUuid = extractUserUuid(authentication);
        log.info("사용자 업데이트 요청 - 사용자 UUID: {}", userUuid);

        UserResponseDto user = userService.updateUserWithValidate(userUuid, updateDto);
        log.info("사용자 업데이트 완료 - 고객 UUID: {}", user.getUserUuid());

        return ResponseEntity.ok(BasicResponseDto.success("사용자 정보를 성공적으로 업데이트 했습니다.",user));
    }

    @PostMapping("/update-password")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update user", description = "Update user password, nickname")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful user info update"),
            @ApiResponse(responseCode = "404", description = "Failed user info update"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public ResponseEntity<BasicResponseDto<UserResponseDto>> updateUserPassword(
            @Valid @RequestBody UserUpdateDto updateDto,
                                                        Authentication authentication){
        UUID userUuid = extractUserUuid(authentication);
        log.info("사용자 비밀번호 업데이트 요청 - 사용자 UUID: {}", userUuid);

        log.info("받은 currentPassword: [{}]", updateDto.getCurrentPassword() );
        log.info("받은 newPassword: [{}]", updateDto.getNewPassword());

        UserResponseDto user = userService.updateUserPassword(userUuid, updateDto);
        log.info("사용자 비밀번호 업데이트 완료 - 고객 UUID: {}", user.getUserUuid());

        return ResponseEntity.ok(BasicResponseDto.success("사용자 정보를 성공적으로 업데이트 했습니다.",user));
    }

    @DeleteMapping("/me")
    @Operation(summary = "delete user", description = "delete user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful user delete"),
            @ApiResponse(responseCode = "404", description = "Failed user delete"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public ResponseEntity<Void> deleteUser(Authentication authentication){

        UUID user = extractUserUuid(authentication);
        userService.deleteUser(user);

        return ResponseEntity.noContent().build();
    }
}

