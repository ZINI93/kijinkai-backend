package com.kijinkai.domain.payment.adapter.in.web;

import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.payment.application.dto.request.DepositRequestDto;
import com.kijinkai.domain.payment.application.dto.response.DepositRequestResponseDto;
import com.kijinkai.domain.payment.application.port.in.deposit.CreateDepositUseCase;
import com.kijinkai.domain.payment.application.port.in.deposit.DeleteDepositUseCase;
import com.kijinkai.domain.payment.application.port.in.deposit.GetDepositUseCase;
import com.kijinkai.domain.payment.application.port.in.deposit.UpdateDepositUseCase;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

import static com.kijinkai.domain.payment.adapter.in.messaging.PaymentMassage.*;
import static com.kijinkai.domain.payment.adapter.in.messaging.PaymentMassage.DEPOSIT_RETRIEVED_SUCCESS;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/payments",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class DepositRequestController {


    private final CreateDepositUseCase createCreatedResponse;
    private final GetDepositUseCase getDepositUseCase;
    private final UpdateDepositUseCase updateDepositUseCase;
    private final DeleteDepositUseCase deleteDepositUseCase;


    // ----  입금 ----

    @Operation(
            summary = "입금요청 생성",
            description = "유저가 입금 요청을 생성 합니다.",
            tags = {"결제관리"}
    )
    @PostMapping("/deposit")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "입금 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "입금 내역을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<DepositRequestResponseDto>> createDeposit(
            Authentication authentication,
            @Valid @RequestBody DepositRequestDto requestDto
    ) {

        UUID userUuid = getUserUuid(authentication);
        log.info("Deposit request created - User: {}, Amount: {}",
                userUuid, requestDto.getAmountOriginal());

        try {
            DepositRequestResponseDto response = createCreatedResponse.processDepositRequest(userUuid, requestDto);
            return createCreatedResponse(DEPOSIT_CREATE_SUCCESS, response, "/api/v1/payments/{requestUuid}", response.getRequestUuid());
        } catch (Exception e) {
            log.error("Failed to process deposit request - User: {}", userUuid, e);
            throw e;
        }
    }


    @Operation(
            summary = "입금 요청 처리",
            description = "유저가 생성한 입금 요청을 처리 합니다",
            tags = {"결제관리"}
    )
    @PostMapping("/admin/deposit/{requestUuid}/approve")
    @PreAuthorize("hasRole('Admin')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "입금 처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "입금 내역을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<DepositRequestResponseDto>> approveDeposit(
            Authentication authentication,
            @PathVariable UUID requestUuid,
            @RequestBody DepositRequestDto requestDto
    ) {
        UUID userUuid = getUserUuid(authentication);
        log.info("Deposit request approved - User: {}", userUuid);

        try {
            DepositRequestResponseDto response = updateDepositUseCase.approveDepositRequest(requestUuid, userUuid, requestDto);
            return createSuccessResponse(DEPOSIT_APPROVE_SUCCESS, response);
        } catch (Exception e) {
            log.error("Failed to process deposit request - admin: {}", userUuid, e);
            throw e;
        }
    }

    @Operation(
            summary = "관리자가 유저의 입금내역을 조회",
            description = "관리를 위해서 관리자가 유저 입금내역을 조회합니다..",
            tags = {"결제관리"}
    )
    @GetMapping("/admin/deposit/{requestUuid}")
    @PreAuthorize("hasRole('Admin')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "입금 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "입금 내역을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<DepositRequestResponseDto>> getDepositRequestInfoByAdmin(
            Authentication authentication,
            @PathVariable UUID requestUuid
    ) {

        UUID userUuid = getUserUuid(authentication);
        log.info("Deposit request retrieved by admin - admin: {}", userUuid);

        try {
            DepositRequestResponseDto response = getDepositUseCase.getDepositRequestInfoByAdmin(requestUuid, userUuid);

            return createSuccessResponse(DEPOSIT_RETRIEVED_SUCCESS, response);
        } catch (Exception e) {
            log.error("Failed to process deposit request - admin: {}", userUuid, e);
            throw e;
        }
    }


    @Operation(
            summary = "관리자가 유저의 입금 승인요청 대기 내역을 조회",
            description = "관리자가 입금 승인을 위해서 내역을 조회합니다.",
            tags = {"결제관리"}
    )
    @GetMapping("/admin/deposits/pending")
    @PreAuthorize("hasRole('Admin')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "입금 요청 내역 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "입금 내역을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<DepositRequestResponseDto>>> getDepositRequestByPending(
            @RequestParam(required = false) String depositorName,
            @PageableDefault(size = 20, page = 0) Pageable pageable,
            Authentication authentication
    ) {

        UUID userUuid = getUserUuid(authentication);
        log.info("Deposits request retrieved by admin - admin: {}", userUuid);

        try {
            Page<DepositRequestResponseDto> response = getDepositUseCase.getDepositsByApprovalPending(userUuid, depositorName, pageable);

            return createSuccessResponse(DEPOSIT_RETRIEVED_SUCCESS, response);
        } catch (Exception e) {
            log.error("Failed to process deposit request - admin: {}", userUuid, e);
            throw e;
        }
    }

    @Operation(
            summary = "입금 내역 전체 조회",
            description = "유저가 입금 내역을 보기 위해 조회합니다.",
            tags = {"결제관리"}
    )
    @PreAuthorize("@paymentSecurityService.canAccessRequest(#requestUuid, authentication.principal)")
    @GetMapping("/deposit/list")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "입금 요청 내역 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "입금 내역을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<DepositRequestResponseDto>>> getDepositDetails(
            @RequestParam(required = false) String depositorName,
            @PageableDefault(size = 20, page = 0) Pageable pageable,
            Authentication authentication
    ) {

        UUID userUuid = getUserUuid(authentication);
        log.info("Deposits request retrieved  - user: {}", userUuid);

        try {
            Page<DepositRequestResponseDto> response = getDepositUseCase.getDeposits(userUuid, pageable);

            return createSuccessResponse(DEPOSIT_RETRIEVED_SUCCESS, response);
        } catch (Exception e) {
            log.error("Failed to process deposit request - admin: {}", userUuid, e);
            throw e;
        }
    }

    @Operation(
            summary = "유저가 본인 입금내역을 조회",
            description = "유저 본인 입금내역을 조회합니다.",
            tags = {"결제관리"}
    )
    @GetMapping("/deposit/{requestUuid}")
    @PreAuthorize("@paymentSecurityService.canAccessRequest(#requestUuid, authentication.principal)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "입금 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "입금 내역을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<DepositRequestResponseDto>> getDepositRequestInfo(
            Authentication authentication,
            @PathVariable UUID requestUuid
    ) {

        UUID userUuid = getUserUuid(authentication);
        log.info("Deposit request retrieved - user: {}", userUuid);

        try {
            DepositRequestResponseDto response = getDepositUseCase.getDepositRequestInfo(requestUuid, userUuid);
            return createSuccessResponse(DEPOSIT_RETRIEVED_SUCCESS, response);
        } catch (Exception e) {
            log.error("Failed to process deposit request - User: {}", userUuid, e);
            throw e;
        }
    }

    // -- helper method

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
