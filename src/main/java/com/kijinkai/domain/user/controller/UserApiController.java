package com.kijinkai.domain.user.controller;


import com.kijinkai.domain.user.dto.UserRequestDto;
import com.kijinkai.domain.user.dto.UserResponseDto;
import com.kijinkai.domain.user.dto.UserUpdateDto;
import com.kijinkai.domain.user.service.CustomUserDetails;
import com.kijinkai.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto requestDto){
        UserResponseDto user = userService.createUserWithValidate(requestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{memberUuid}")
                .buildAndExpand(user.getUserUuid())
                .toUri();
        return ResponseEntity.created(location).body(user);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getUserInfo(Authentication authentication){
        UUID userUuid = customUserDetails(authentication);

        UserResponseDto user = userService.getUserInfo(userUuid);
        return ResponseEntity.ok(user);
    }

    private static UUID getUuid(CustomUserDetails customUserDetails) {
        return customUserDetails.getUserUuid();
    }


    @PutMapping("/update")
    public ResponseEntity<UserResponseDto> editUser(@Valid @RequestBody UserUpdateDto updateDto,
                                                        Authentication authentication){
        UUID userUuid = customUserDetails(authentication);

        UserResponseDto updatedMember = userService.updateUserWithValidate(userUuid, updateDto);
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/delete")
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

