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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "Users management API")
public class UserApiController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "user register", description = "Receive and save user email, password, nickname")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful membership registration"),
            @ApiResponse(responseCode = "404", description = "Failed membership registration"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public ResponseEntity<BasicResponseDto<UserResponseDto>> createUser(@Valid @RequestBody UserRequestDto requestDto){
        UserResponseDto user = userService.createUserWithValidate(requestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{memberUuid}")
                .buildAndExpand(user.getUserUuid())
                .toUri();
        return ResponseEntity.created(location).body(BasicResponseDto.success("Successful join", user));
    }

    @GetMapping("/me")
    @Operation(summary = "User info Inquiry", description = "User info Inquiry")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful user info Inquiry"),
            @ApiResponse(responseCode = "404", description = "Failed user info Inquiry"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public ResponseEntity<UserResponseDto> getUserInfo(Authentication authentication){
        UUID userUuid = customUserDetails(authentication);

        UserResponseDto user = userService.getUserInfo(userUuid);
        return ResponseEntity.ok(user);
    }

    private static UUID getUuid(CustomUserDetails customUserDetails) {
        return customUserDetails.getUserUuid();
    }


    @PutMapping("/update")
    @Operation(summary = "Update user", description = "Update user password, nickname")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful user info update"),
            @ApiResponse(responseCode = "404", description = "Failed user info update"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public ResponseEntity<UserResponseDto> editUser(@Valid @RequestBody UserUpdateDto updateDto,
                                                        Authentication authentication){
        UUID userUuid = customUserDetails(authentication);

        UserResponseDto updatedMember = userService.updateUserWithValidate(userUuid, updateDto);
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete user", description = "delete user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful user delete"),
            @ApiResponse(responseCode = "404", description = "Failed user delete"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public ResponseEntity<Void> deleteUser(Authentication authentication){

        UUID user = customUserDetails(authentication);
        userService.deleteUser(user);

        return ResponseEntity.noContent().build();
    }

    private static UUID customUserDetails(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return getUuid(customUserDetails);
    }
}

