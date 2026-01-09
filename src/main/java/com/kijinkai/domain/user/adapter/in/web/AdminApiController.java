package com.kijinkai.domain.user.adapter.in.web;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.user.application.dto.response.UserResponseDto;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import com.kijinkai.domain.user.application.port.in.GetUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RequestMapping("/api/v1/admin")
@RestController
@RequiredArgsConstructor
@Tag(name = "admin", description = "Admin management API")
public class AdminApiController {


    private final GetUserUseCase getUserUseCase;



    @GetMapping("/user-list")
    @Operation(summary = "유저 리스트 조회", description = "관리자가 유저 리스트를 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "유저 리스트 조회 성공"),
            @ApiResponse(responseCode = "404", description = "유저 정보를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public ResponseEntity<BasicResponseDto<Page<UserResponseDto>>> getUsersList(Authentication authentication,
                                                                                @RequestParam(required = false) String email,
                                                                                @RequestParam(required = false) String name,
                                                                                @PageableDefault(size = 20, page = 0) Pageable pageable) {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userUuid = customUserDetails.getUserUuid();

        Page<UserResponseDto> allByUsers = getUserUseCase.findAllByEmailAndNickName(userUuid, email, name, pageable);

        return ResponseEntity.ok(BasicResponseDto.success("회원 리스트 조회 성공", allByUsers));
    }
}
