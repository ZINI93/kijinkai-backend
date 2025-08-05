package com.kijinkai.domain.wallet.controller;

import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.user.service.CustomUserDetails;
import com.kijinkai.domain.wallet.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(
        value = "/api/v1/wallets",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class WalletApiController {

    private final WalletService walletService;


    @GetMapping("/me")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "월렛 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "월렛을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<WalletResponseDto>> getWalletInfo(Authentication authentication){

        UUID userUuid = getUserUuid(authentication);
        WalletResponseDto wallet = walletService.getWalletBalance(userUuid);
        return ResponseEntity.ok(BasicResponseDto.success("고객 정보 조회 성공", wallet));
    }

    private static UUID getUserUuid(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUserUuid();
    }

}
