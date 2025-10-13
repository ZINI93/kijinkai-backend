package com.kijinkai.domain.wallet.adapter.in.web;

import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import com.kijinkai.domain.wallet.application.dto.WalletFreezeRequest;
import com.kijinkai.domain.wallet.application.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.application.service.WalletApplicationService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') and @securityService.canAccessWallet(#walletUuid)")
@RequestMapping(
        value = "/api/v1/wallets",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class WalletAdminAPiController {

    private final WalletApplicationService walletApplicationService;

    /**
     * 규악 위반 유저 월렛 동결
     * @param walletUuid
     * @param reason
     * @param authentication
     * @return
     */
    @PutMapping("/{walletUuid}/freeze")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "월렛 정지 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "월렛을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<WalletResponseDto>> freezeWallet(
            @PathVariable UUID walletUuid,
            @Valid @RequestBody WalletFreezeRequest request,
            Authentication authentication
    ) {

        UUID adminUuid = getUserUuid(authentication);
        log.info("관리자 {}가 월렛 {} 동결 요청", adminUuid, walletUuid);

        WalletResponseDto wallet = walletApplicationService.freezeWallet(adminUuid, walletUuid, request);
        log.info("월렛 동결 완료:{}", walletUuid);


        return ResponseEntity.ok(BasicResponseDto.success("Successful freeze user wallet", wallet));
    }

    /**
     * 월렛 활성상태로 복원
     * @param walletUuid
     * @param authentication
     * @return
     */
    @PutMapping("/{walletUuid}/unfreeze")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "월렛 복원 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "월렛을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<WalletResponseDto>> unfreezeWallet(
            @PathVariable UUID walletUuid,
            Authentication authentication
    ) {
        UUID adminUuid = getUserUuid(authentication);
        log.info("관리자 {}가 월렛 {} 동결 해제 요청", adminUuid, walletUuid);

        WalletResponseDto wallet = walletApplicationService.unFreezeWallet(adminUuid, walletUuid);
        log.info("월렛 동결해제 완료:{}", walletUuid);

        return ResponseEntity.ok(BasicResponseDto.success("Successful unFreeze user wallet", wallet));
    }

    /**
     *  유저의 월렛 검색
     * @param walletUuid
     * @param authentication
     * @return
     */
    @GetMapping("/{walletUuid}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "월렛 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "월렛을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<WalletResponseDto>> getUserWalletInfo(
            @PathVariable String walletUuid,
            Authentication authentication
    ) {

        UUID adminUuid = getUserUuid(authentication);
        log.info("관리자 {}가 월렛 {} 정보검색 요청", adminUuid, walletUuid);

        WalletResponseDto wallet = walletApplicationService.getCustomerWalletBalanceByAdmin(adminUuid, walletUuid);
        log.info("관리자가 월렛: {} 검색 완료", walletUuid);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved user wallet information", wallet));

    }

    private static UUID getUserUuid(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUserUuid();
    }
}
