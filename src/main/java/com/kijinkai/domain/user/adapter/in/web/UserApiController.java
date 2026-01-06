package com.kijinkai.domain.user.adapter.in.web;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import com.kijinkai.domain.user.application.dto.UserRequestDto;
import com.kijinkai.domain.user.application.dto.UserResponseDto;
import com.kijinkai.domain.user.application.dto.UserUpdateDto;
import com.kijinkai.domain.user.application.port.in.CreateUserUseCase;
import com.kijinkai.domain.user.application.port.in.DeleteUserUseCase;
import com.kijinkai.domain.user.application.port.in.GetUserUseCase;
import com.kijinkai.domain.user.application.port.in.UpdateUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Tag(name = "User", description = "Users management API")
@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
public class UserApiController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUser;


    /**
     * 중복체크
     * @param requestDto
     * @return
     */
    @PostMapping(value = "/users/exists")
    public ResponseEntity<Boolean> existsByUser(
            @Validated(UserRequestDto.existsGroup.class) @RequestBody UserRequestDto requestDto
    ) {

        return ResponseEntity.ok(getUserUseCase.existsByUser(requestDto));
    }


    /**
     * 회원가입
     *
     * @param requestDto
     * @return
     */
    @PostMapping(value = "/users/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "user register", description = "Receive and save user email, password, nickname")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successful membership registration"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Email already exists"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public ResponseEntity<BasicResponseDto<UserResponseDto>> createUser(
            @Validated({UserRequestDto.createGroup.class, UserRequestDto.passwordGroup.class}) @RequestBody UserRequestDto requestDto) {

//        log.info("사용자 회원가입 요청 - 이메일: {}", requestDto.getEmail());

        UserResponseDto user = createUserUseCase.createUser(requestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{userUuid}")
                .buildAndExpand(user.getUserUuid())
                .toUri();

        return ResponseEntity.created(location).body(BasicResponseDto.success("Successful created user", user));
    }

    /**
     * 본인 계정 조회
     *
     * @param customUserDetails
     * @return
     * @throws AccessDeniedException
     */
    @GetMapping(value = "/api/v1/users/me")
    @Operation(summary = "User info Inquiry", description = "User info Inquiry")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful user info Inquiry"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Failed user info Inquiry"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public ResponseEntity<BasicResponseDto<UserResponseDto>> getUserInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) throws AccessDeniedException {

        UUID userUuid = customUserDetails.getUserUuid();
        UserResponseDto user = getUserUseCase.getUserInfo(userUuid);
        log.info("사용자 조회 완료 - 고객 UUID: {}", user.getUserUuid());

        return ResponseEntity.ok(BasicResponseDto.success("Successful retrieved user information", user));
    }


    /**
     * 프로필 업데이트
     *
     * @param updateDto
     * @param customUserDetails
     * @return
     */
    @PutMapping(value = "/api/v1/users/me", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update user", description = "Update user password, nickname")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful user info update"),
            @ApiResponse(responseCode = "404", description = "Failed user info update"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public ResponseEntity<BasicResponseDto<UserResponseDto>> updateUser(@Validated(UserRequestDto.updateGroup.class) @RequestBody UserUpdateDto updateDto,
                                                                        @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        UUID userUuid = customUserDetails.getUserUuid();

        UserResponseDto user = updateUserUseCase.updateUserProfile(userUuid, updateDto);
        log.info("사용자 업데이트 완료 - 고객 UUID: {}", user.getUserUuid());

        return ResponseEntity.ok(BasicResponseDto.success("Successful updated user profile", user));
    }

    /**
     * 비밀번호 업데이트
     *
     * @param updateDto
     * @param customUserDetails
     * @return
     */
    @PostMapping(value = "/api/v1/users/update-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update user", description = "Update user password, nickname")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful user info update"),
            @ApiResponse(responseCode = "404", description = "Failed user info update"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public ResponseEntity<BasicResponseDto<UserResponseDto>> updateUserPassword(
            @Validated(UserRequestDto.passwordGroup.class) @RequestBody UserUpdateDto updateDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        UUID userUuid = customUserDetails.getUserUuid();

        UserResponseDto user = updateUserUseCase.updateUserPassword(userUuid, updateDto);
        log.info("사용자 비밀번호 업데이트 완료 - 고객 UUID: {}", user.getUserUuid());

        return ResponseEntity.ok(BasicResponseDto.success("Successful updated user password", user));
    }

    /**
     * 계정 삭제
     *
     * @param customUserDetails
     * @return
     * @throws AccessDeniedException
     */
    @DeleteMapping(value = "/api/v1/users/me")
    @Operation(summary = "delete user", description = "delete user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful user delete"),
            @ApiResponse(responseCode = "404", description = "Failed user delete"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) throws AccessDeniedException {

        UUID userUuid = customUserDetails.getUserUuid();

        deleteUser.deleteUser(userUuid);

        return ResponseEntity.noContent().build();
    }
}


