package com.kijinkai.domain.payment.adapter.in.web;

import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.payment.application.dto.request.WithdrawRequestDto;
import com.kijinkai.domain.payment.application.dto.response.WithdrawResponseDto;
import com.kijinkai.domain.payment.application.port.in.withdraw.CreateWithdrawUseCase;
import com.kijinkai.domain.payment.application.port.in.withdraw.DeleteWithdrawUseCase;
import com.kijinkai.domain.payment.application.port.in.withdraw.GetWithdrawUseCase;
import com.kijinkai.domain.payment.application.port.in.withdraw.UpdateWithdrawUseCase;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

import static com.kijinkai.domain.payment.adapter.in.messaging.PaymentMassage.*;
import static com.kijinkai.domain.payment.adapter.in.messaging.PaymentMassage.WITHDRAW_RETRIEVED_SUCCESS;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/withdraws",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class WithdrawRequestController {

    private final CreateWithdrawUseCase createWithdrawUseCase;
    private final GetWithdrawUseCase getWithdrawUseCase;
    private final UpdateWithdrawUseCase updateWithdrawUseCase;
    private final DeleteWithdrawUseCase deleteWithdrawUseCase;


    //  ---- 출금 ----

    @Operation(
            summary = "유저가 출금 요청을 생성",
            description = "유저가 출금위해 관리자에게 요청을 생성합니다",
            tags = {"결제관리"}
    )
    @PostMapping("/create")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "출금 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "출금내역을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<WithdrawResponseDto>> createWithdraw(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody WithdrawRequestDto requestDto
    ) {

        log.info("Withdraw request created - User: {}, Amount: {}",
                customUserDetails.getUserUuid(), requestDto.getRequestAmount());

        try {
            WithdrawResponseDto response = createWithdrawUseCase.processWithdrawRequest(customUserDetails.getUserUuid(), requestDto);
            return createCreatedResponse(WITHDRAW_CREATE_SUCCESS, response, "/api/v1/payments/withdraw/{requestUuid}", response.getRequestUuid());
        } catch (Exception e) {
            log.error("Failed to process withdraw request - User: {}", customUserDetails.getUserUuid(), e);
            throw e;
        }
    }




    @Operation(
            summary = "관리자가 유저의 출금 내역조회",
            description = "관리자가 관리를 위해 유저의 출금 내역을 조회합니다",
            tags = {"결제관리"}
    )
    @GetMapping("/admin/withdraw/{requestUuid}")
    @PreAuthorize("hasRole('Admin')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "출금 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "출금내역을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<WithdrawResponseDto>> getWithdrawRequestInfoByAdmin(
            Authentication authentication,
            @PathVariable UUID requestUuid
    ) {
        UUID userUuid = getUserUuid(authentication);
        log.info("Withdraw request retrieved by admin - admin: {}", userUuid);

        try {
            WithdrawResponseDto response = getWithdrawUseCase.getWithdrawInfoByAdmin(requestUuid, userUuid);
            return createSuccessResponse(WITHDRAW_RETRIEVED_SUCCESS, response);
        } catch (Exception e) {
            log.error("Failed to process withdraw request - admin: {}", userUuid, e);
            throw e;
        }
    }


    @Operation(
            summary = "유저가 출금 내역 조회",
            description = "유저가 본인 출금 내역을 조회 합니다.",
            tags = {"결제관리"}
    )
    @GetMapping("/withdraw/{requestUuid}")
    @PreAuthorize("@paymentSecurityService.canAccessRequest(#requestUuid, authentication.principal)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "출금 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "출금내역을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<WithdrawResponseDto>> getWithdrawRequestInfo(
            Authentication authentication,
            @PathVariable UUID requestUuid
    ) {
        UUID userUuid = getUserUuid(authentication);
        log.info("Withdraw request retrieved - user: {}", userUuid);

        try {
            WithdrawResponseDto response = getWithdrawUseCase.getWithdrawInfo(requestUuid, userUuid);
            return createSuccessResponse(WITHDRAW_RETRIEVED_SUCCESS, response);
        } catch (Exception e) {
            log.error("Failed to process withdraw request - User: {}", userUuid, e);
            throw e;
        }
    }




    @Operation(
            summary = "출금 내역 전체 조회",
            description = "유저가 출금 내역을 보기 위해 조회합니다.",
            tags = {"결제관리"}
    )
    @PreAuthorize("@paymentSecurityService.canAccessRequest(#requestUuid, authentication.principal)")
    @GetMapping("/withdraw/list")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "입금 요청 내역 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "입금 내역을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<WithdrawResponseDto>>> getWithdrawDetails(
            @PageableDefault(size = 20, page = 0) Pageable pageable,
            Authentication authentication
    ) {

        UUID userUuid = getUserUuid(authentication);
        log.info("Withdraw request retrieved  - user: {}", userUuid);

        try {
            Page<WithdrawResponseDto> response = getWithdrawUseCase.getWithdraws(userUuid, pageable);

            return createSuccessResponse(WITHDRAW_RETRIEVED_SUCCESS, response);
        } catch (Exception e) {
            log.error("Failed to process deposit request - admin: {}", userUuid, e);
            throw e;
        }
    }

    private static UUID getUserUuid(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUserUuid();
    }

    private <T> ResponseEntity<BasicResponseDto<T>> createSuccessResponse(
            String message, T data) {
        return ResponseEntity.ok(BasicResponseDto.success(message, data));
    }

    private <T> ResponseEntity<BasicResponseDto<T>> createCreatedResponse(
            String message,
            T data,
            String path,
            Object... pathVariables) {
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(path)
                .buildAndExpand(pathVariables)
                .toUri();
        return ResponseEntity.created(location)
                .body(BasicResponseDto.success(message, data));
    }
}
