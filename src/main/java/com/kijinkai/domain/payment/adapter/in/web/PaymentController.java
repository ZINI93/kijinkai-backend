//package com.kijinkai.domain.payment.adapter.in.web;
//
//
//import com.kijinkai.domain.common.BasicResponseDto;
//import com.kijinkai.domain.payment.application.port.in.PaymentUseCase;
//import com.kijinkai.domain.payment.application.dto.request.DepositRequestDto;
//import com.kijinkai.domain.payment.application.dto.request.RefundRequestDto;
//import com.kijinkai.domain.payment.application.dto.request.WithdrawRequestDto;
//import com.kijinkai.domain.payment.application.dto.response.DepositRequestResponseDto;
//import com.kijinkai.domain.payment.application.dto.response.RefundResponseDto;
//import com.kijinkai.domain.payment.application.dto.response.WithdrawResponseDto;
//import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
//
//
//import java.net.URI;
//import java.util.UUID;
//
//import static com.kijinkai.domain.payment.adapter.in.messaging.PaymentMassage.*;
//
//@Slf4j
//@RequiredArgsConstructor
//@RestController
//@RequestMapping(
//        value = "/api/v1/payments",
//        produces = MediaType.APPLICATION_JSON_VALUE
//)
//public class PaymentController {
//
//    private final PaymentUseCase paymentUseCase;
//
//    // ----  입금 ----
//
//    @Operation(
//            summary = "입금요청 생성",
//            description = "유저가 입금 요청을 생성 합니다.",
//            tags = {"결제관리"}
//    )
//    @PostMapping("/deposit")
//    @ApiResponses({
//            @ApiResponse(responseCode = "201", description = "입금 생성 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
//            @ApiResponse(responseCode = "404", description = "입금 내역을 찾을 수 없음"),
//            @ApiResponse(responseCode = "500", description = "서버오류")
//    })
//    public ResponseEntity<BasicResponseDto<DepositRequestResponseDto>> createDeposit(
//            Authentication authentication,
//            @Valid @RequestBody DepositRequestDto requestDto
//    ) {
//
//        UUID userUuid = getUserUuid(authentication);
//        log.info("Deposit request created - User: {}, Amount: {}",
//                userUuid, requestDto.getAmountOriginal());
//
//        try {
//            DepositRequestResponseDto response = paymentUseCase.processDepositRequest(userUuid, requestDto);
//            return createCreatedResponse(DEPOSIT_CREATE_SUCCESS, response, "/api/v1/payments/{requestUuid}", response.getRequestUuid());
//        } catch (Exception e) {
//            log.error("Failed to process deposit request - User: {}", userUuid, e);
//            throw e;
//        }
//    }
//
//
//    @Operation(
//            summary = "입금 요청 처리",
//            description = "유저가 생성한 입금 요청을 처리 합니다",
//            tags = {"결제관리"}
//    )
//    @PostMapping("/admin/deposit/{requestUuid}/approve")
//    @PreAuthorize("hasRole('Admin')")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "입금 처리 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
//            @ApiResponse(responseCode = "404", description = "입금 내역을 찾을 수 없음"),
//            @ApiResponse(responseCode = "500", description = "서버오류")
//    })
//    public ResponseEntity<BasicResponseDto<DepositRequestResponseDto>> approveDeposit(
//            Authentication authentication,
//            @PathVariable UUID requestUuid,
//            @RequestBody DepositRequestDto requestDto
//    ) {
//        UUID userUuid = getUserUuid(authentication);
//        log.info("Deposit request approved - User: {}", userUuid);
//
//        try {
//            DepositRequestResponseDto response = paymentUseCase.approveDepositRequest(requestUuid, userUuid, requestDto);
//            return createSuccessResponse(DEPOSIT_APPROVE_SUCCESS, response);
//        } catch (Exception e) {
//            log.error("Failed to process deposit request - admin: {}", userUuid, e);
//            throw e;
//        }
//    }
//
//    @Operation(
//            summary = "관리자가 유저의 입금내역을 조회",
//            description = "관리를 위해서 관리자가 유저 입금내역을 조회합니다..",
//            tags = {"결제관리"}
//    )
//    @GetMapping("/admin/deposit/{requestUuid}")
//    @PreAuthorize("hasRole('Admin')")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "입금 조회 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
//            @ApiResponse(responseCode = "404", description = "입금 내역을 찾을 수 없음"),
//            @ApiResponse(responseCode = "500", description = "서버오류")
//    })
//    public ResponseEntity<BasicResponseDto<DepositRequestResponseDto>> getDepositRequestInfoByAdmin(
//            Authentication authentication,
//            @PathVariable UUID requestUuid
//    ) {
//
//        UUID userUuid = getUserUuid(authentication);
//        log.info("Deposit request retrieved by admin - admin: {}", userUuid);
//
//        try {
//            DepositRequestResponseDto response = paymentUseCase.getDepositRequestInfoByAdmin(requestUuid, userUuid);
//
//            return createSuccessResponse(DEPOSIT_RETRIEVED_SUCCESS, response);
//        } catch (Exception e) {
//            log.error("Failed to process deposit request - admin: {}", userUuid, e);
//            throw e;
//        }
//    }
//
//
//    @Operation(
//            summary = "관리자가 유저의 입금 승인요청 대기 내역을 조회",
//            description = "관리자가 입금 승인을 위해서 내역을 조회합니다.",
//            tags = {"결제관리"}
//    )
//    @GetMapping("/admin/deposits/pending")
//    @PreAuthorize("hasRole('Admin')")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "입금 요청 내역 조회 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
//            @ApiResponse(responseCode = "404", description = "입금 내역을 찾을 수 없음"),
//            @ApiResponse(responseCode = "500", description = "서버오류")
//    })
//    public ResponseEntity<BasicResponseDto<Page<DepositRequestResponseDto>>> getDepositRequestByPending(
//            @RequestParam(required = false) String depositorName,
//            @PageableDefault(size = 20, page = 0) Pageable pageable,
//            Authentication authentication
//    ) {
//
//        UUID userUuid = getUserUuid(authentication);
//        log.info("Deposits request retrieved by admin - admin: {}", userUuid);
//
//        try {
//            Page<DepositRequestResponseDto> response = paymentUseCase.getDepositsByApprovalPending(userUuid, depositorName, pageable);
//
//            return createSuccessResponse(DEPOSIT_RETRIEVED_SUCCESS, response);
//        } catch (Exception e) {
//            log.error("Failed to process deposit request - admin: {}", userUuid, e);
//            throw e;
//        }
//    }
//
//    @Operation(
//            summary = "입금 내역 전체 조회",
//            description = "유저가 입금 내역을 보기 위해 조회합니다.",
//            tags = {"결제관리"}
//    )
//    @PreAuthorize("@paymentSecurityService.canAccessRequest(#requestUuid, authentication.principal)")
//    @GetMapping("/deposit/list")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "입금 요청 내역 조회 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
//            @ApiResponse(responseCode = "404", description = "입금 내역을 찾을 수 없음"),
//            @ApiResponse(responseCode = "500", description = "서버오류")
//    })
//    public ResponseEntity<BasicResponseDto<Page<DepositRequestResponseDto>>> getDepositDetails(
//            @RequestParam(required = false) String depositorName,
//            @PageableDefault(size = 20, page = 0) Pageable pageable,
//            Authentication authentication
//    ) {
//
//        UUID userUuid = getUserUuid(authentication);
//        log.info("Deposits request retrieved  - user: {}", userUuid);
//
//        try {
//            Page<DepositRequestResponseDto> response = paymentUseCase.getDeposits(userUuid, pageable);
//
//            return createSuccessResponse(DEPOSIT_RETRIEVED_SUCCESS, response);
//        } catch (Exception e) {
//            log.error("Failed to process deposit request - admin: {}", userUuid, e);
//            throw e;
//        }
//    }
//
//    @Operation(
//            summary = "유저가 본인 입금내역을 조회",
//            description = "유저 본인 입금내역을 조회합니다.",
//            tags = {"결제관리"}
//    )
//    @GetMapping("/deposit/{requestUuid}")
//    @PreAuthorize("@paymentSecurityService.canAccessRequest(#requestUuid, authentication.principal)")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "입금 조회 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
//            @ApiResponse(responseCode = "404", description = "입금 내역을 찾을 수 없음"),
//            @ApiResponse(responseCode = "500", description = "서버오류")
//    })
//    public ResponseEntity<BasicResponseDto<DepositRequestResponseDto>> getDepositRequestInfo(
//            Authentication authentication,
//            @PathVariable UUID requestUuid
//    ) {
//
//        UUID userUuid = getUserUuid(authentication);
//        log.info("Deposit request retrieved - user: {}", userUuid);
//
//        try {
//            DepositRequestResponseDto response = paymentUseCase.getDepositRequestInfo(requestUuid, userUuid);
//            return createSuccessResponse(DEPOSIT_RETRIEVED_SUCCESS, response);
//        } catch (Exception e) {
//            log.error("Failed to process deposit request - User: {}", userUuid, e);
//            throw e;
//        }
//    }
//
//    //  ---- 출금 ----
//
//    @Operation(
//            summary = "유저가 출금 요청을 생성",
//            description = "유저가 출금위해 관리자에게 요청을 생성합니다",
//            tags = {"결제관리"}
//    )
//    @PostMapping("/withdraw")
//    @ApiResponses({
//            @ApiResponse(responseCode = "201", description = "출금 생성 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
//            @ApiResponse(responseCode = "404", description = "출금내역을 찾을 수 없음"),
//            @ApiResponse(responseCode = "500", description = "서버오류")
//    })
//    public ResponseEntity<BasicResponseDto<WithdrawResponseDto>> createWithdraw(
//            Authentication authentication,
//            @Valid @RequestBody WithdrawRequestDto requestDto
//    ) {
//
//        UUID userUuid = getUserUuid(authentication);
//        log.info("Withdraw request created - User: {}, Amount: {}",
//                userUuid, requestDto.getRequestAmount());
//
//        try {
//            WithdrawResponseDto response = paymentUseCase.processWithdrawRequest(userUuid, requestDto);
//            return createCreatedResponse(WITHDRAW_CREATE_SUCCESS, response, "/api/v1/payments/withdraw/{requestUuid}", response.getRequestUuid());
//        } catch (Exception e) {
//            log.error("Failed to process withdraw request - User: {}", userUuid, e);
//            throw e;
//        }
//    }
//
//    @Operation(
//            summary = "관리자가 출금 요청 처리",
//            description = "관리자가 유저가 요청한 출금 요청을 처리 합니다.",
//            tags = {"결제관리"}
//    )
//    @PostMapping("/admin/withdraw/{requestUuid}/approve")
//    @PreAuthorize("hasRole('Admin')")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "입금 처리 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
//            @ApiResponse(responseCode = "404", description = "출금내역을 찾을 수 없음"),
//            @ApiResponse(responseCode = "500", description = "서버오류")
//    })
//    public ResponseEntity<BasicResponseDto<WithdrawResponseDto>> approveWithdraw(
//            Authentication authentication,
//            @PathVariable UUID requestUuid,
//            @RequestBody WithdrawRequestDto requestDto
//    ) {
//        UUID userUuid = getUserUuid(authentication);
//        log.info("Withdraw request approved - admin: {}", userUuid);
//
//        try {
//            WithdrawResponseDto response = paymentUseCase.approveWithdrawRequest(requestUuid, userUuid, requestDto);
//            return createSuccessResponse(WITHDRAW_APPROVE_SUCCESS, response);
//        } catch (Exception e) {
//            log.error("Failed to process withdraw request - admin: {}", userUuid, e);
//            throw e;
//        }
//    }
//
//
//    @Operation(
//            summary = "관리자가 유저의 출금 내역조회",
//            description = "관리자가 관리를 위해 유저의 출금 내역을 조회합니다",
//            tags = {"결제관리"}
//    )
//    @GetMapping("/admin/withdraw/{requestUuid}")
//    @PreAuthorize("hasRole('Admin')")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "출금 조회 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
//            @ApiResponse(responseCode = "404", description = "출금내역을 찾을 수 없음"),
//            @ApiResponse(responseCode = "500", description = "서버오류")
//    })
//    public ResponseEntity<BasicResponseDto<WithdrawResponseDto>> getWithdrawRequestInfoByAdmin(
//            Authentication authentication,
//            @PathVariable UUID requestUuid
//    ) {
//        UUID userUuid = getUserUuid(authentication);
//        log.info("Withdraw request retrieved by admin - admin: {}", userUuid);
//
//        try {
//            WithdrawResponseDto response = paymentUseCase.getWithdrawInfoByAdmin(requestUuid, userUuid);
//            return createSuccessResponse(WITHDRAW_RETRIEVED_SUCCESS, response);
//        } catch (Exception e) {
//            log.error("Failed to process withdraw request - admin: {}", userUuid, e);
//            throw e;
//        }
//    }
//
//
//    @Operation(
//            summary = "유저가 출금 내역 조회",
//            description = "유저가 본인 출금 내역을 조회 합니다.",
//            tags = {"결제관리"}
//    )
//    @GetMapping("/withdraw/{requestUuid}")
//    @PreAuthorize("@paymentSecurityService.canAccessRequest(#requestUuid, authentication.principal)")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "출금 조회 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
//            @ApiResponse(responseCode = "404", description = "출금내역을 찾을 수 없음"),
//            @ApiResponse(responseCode = "500", description = "서버오류")
//    })
//    public ResponseEntity<BasicResponseDto<WithdrawResponseDto>> getWithdrawRequestInfo(
//            Authentication authentication,
//            @PathVariable UUID requestUuid
//    ) {
//        UUID userUuid = getUserUuid(authentication);
//        log.info("Withdraw request retrieved - user: {}", userUuid);
//
//        try {
//            WithdrawResponseDto response = paymentUseCase.getWithdrawInfo(requestUuid, userUuid);
//            return createSuccessResponse(WITHDRAW_RETRIEVED_SUCCESS, response);
//        } catch (Exception e) {
//            log.error("Failed to process withdraw request - User: {}", userUuid, e);
//            throw e;
//        }
//    }
//
//
//    @Operation(
//            summary = "관리자가 유저의 출금 승인요청 대기 내역을 조회",
//            description = "관리자가 출금 승인을 위해서 내역을 조회합니다.",
//            tags = {"결제관리"}
//    )
//    @GetMapping("/admin/withdraws/pending")
//    @PreAuthorize("hasRole('Admin')")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "입금 요청 내역 조회 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
//            @ApiResponse(responseCode = "404", description = "입금 내역을 찾을 수 없음"),
//            @ApiResponse(responseCode = "500", description = "서버오류")
//    })
//    public ResponseEntity<BasicResponseDto<Page<WithdrawResponseDto>>> getWithdrawRequestByPending(
//            @RequestParam(required = false) String withdrawName,
//            @PageableDefault(size = 20, page = 0) Pageable pageable,
//            Authentication authentication
//    ) {
//
//        UUID userUuid = getUserUuid(authentication);
//        log.info("Withdraws request retrieved by admin - admin: {}", userUuid);
//
//        try {
//            Page<WithdrawResponseDto> response = paymentUseCase.getWithdrawByApprovalPending(userUuid, withdrawName, pageable);
//
//            return createSuccessResponse(WITHDRAW_RETRIEVED_SUCCESS, response);
//        } catch (Exception e) {
//            log.error("Failed to process deposit request - admin: {}", userUuid, e);
//            throw e;
//        }
//    }
//
//    @Operation(
//            summary = "출금 내역 전체 조회",
//            description = "유저가 출금 내역을 보기 위해 조회합니다.",
//            tags = {"결제관리"}
//    )
//    @PreAuthorize("@paymentSecurityService.canAccessRequest(#requestUuid, authentication.principal)")
//    @GetMapping("/withdraw/list")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "입금 요청 내역 조회 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
//            @ApiResponse(responseCode = "404", description = "입금 내역을 찾을 수 없음"),
//            @ApiResponse(responseCode = "500", description = "서버오류")
//    })
//    public ResponseEntity<BasicResponseDto<Page<WithdrawResponseDto>>> getWithdrawDetails(
//            @PageableDefault(size = 20, page = 0) Pageable pageable,
//            Authentication authentication
//    ) {
//
//        UUID userUuid = getUserUuid(authentication);
//        log.info("Withdraw request retrieved  - user: {}", userUuid);
//
//        try {
//            Page<WithdrawResponseDto> response = paymentUseCase.getWithdraws(userUuid, pageable);
//
//            return createSuccessResponse(WITHDRAW_RETRIEVED_SUCCESS, response);
//        } catch (Exception e) {
//            log.error("Failed to process deposit request - admin: {}", userUuid, e);
//            throw e;
//        }
//    }
//
//    //  ---- 환불 ----
//
//    @Operation(
//            summary = "관리자가 상품에 대한 환불처리",
//            description = "관리자가 유저가 요청한 상품리스트에서 흭득을 하지 못한 상품을 환불 처리해줍니다.",
//            tags = {"결제관리"}
//    )
//    @PostMapping("/{orderItemUuid}/refund-request")
//    @PreAuthorize("hasRole('Admin')")
//    @ApiResponses({
//            @ApiResponse(responseCode = "201", description = "환불 생성 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
//            @ApiResponse(responseCode = "404", description = "환불내역을 찾을 수 없음"),
//            @ApiResponse(responseCode = "500", description = "서버오류")
//    })
//    public ResponseEntity<BasicResponseDto<RefundResponseDto>> createRefund(
//            Authentication authentication,
//            @Valid @RequestBody RefundRequestDto requestDto,
//            @PathVariable UUID orderItemUuid
//    ) {
//        UUID userUuid = getUserUuid(authentication);
//        log.info("Refund request created - User: {}, OrderItem: {}",
//                userUuid, orderItemUuid);
//
//        try {
//            RefundResponseDto response = paymentUseCase.processRefundRequest(userUuid, orderItemUuid, requestDto);
//            return createCreatedResponse(REFUND_CREATE_SUCCESS, response, "/api/v1/payments/refund/{refundUuid}", response.getRefundUuid());
//        } catch (Exception e) {
//            log.error("Failed to process refund request - admin: {}", userUuid, e);
//
//            throw e;
//        }
//    }
//
//
//    @Operation(
//            summary = "생성된 환불을 확인 후 처리",
//            description = "관리자 본인이 생성한 환불을 확인 후 처리",
//            tags = {"결제관리"}
//    )
//    @PutMapping("/admin/refund/{refundUuid}/approve")
//    @PreAuthorize("hasRole('Admin')")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "환불 처리 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
//            @ApiResponse(responseCode = "404", description = "환불내역을 찾을 수 없음"),
//            @ApiResponse(responseCode = "500", description = "서버오류")
//    })
//    public ResponseEntity<BasicResponseDto<RefundResponseDto>> approveRefund(
//            Authentication authentication,
//            @PathVariable UUID refundUuid,
//            @RequestBody String memo
//    ) {
//        UUID userUuid = getUserUuid(authentication);
//        log.info("Refund request approved - admin: {}", userUuid);
//
//        try {
//
//            RefundResponseDto response = paymentUseCase.approveRefundRequest(refundUuid, userUuid, memo);
//
//            return createSuccessResponse(REFUND_APPROVE_SUCCESS, response);
//        } catch (Exception e) {
//            log.error("Failed to process refund request - admin: {}", userUuid, e);
//            throw e;
//        }
//    }
//
//    @Operation(
//            summary = "관리자가 환불내역 조회",
//            description = "환불 내역조회",
//            tags = {"결제관리"}
//    )
//    @GetMapping("/admin/refund/{refundUuid}")
//    @PreAuthorize("hasRole('Admin')")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "환불 조회 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
//            @ApiResponse(responseCode = "404", description = "환불내역을 찾을 수 없음"),
//            @ApiResponse(responseCode = "500", description = "서버오류")
//    })
//    public ResponseEntity<BasicResponseDto<RefundResponseDto>> getRefundRequestInfoByAdmin(
//            Authentication authentication,
//            @PathVariable UUID refundUuid
//    ) {
//        UUID userUuid = getUserUuid(authentication);
//        log.info("Refund request retrieved by admin - admin: {}", userUuid);
//
//        try {
//            RefundResponseDto response = paymentUseCase.getRefundInfoByAdmin(refundUuid, userUuid);
//
//
//            return createSuccessResponse(REFUND_RETRIEVED_SUCCESS, response);
//        } catch (Exception e) {
//            log.error("Failed to process refund request - admin: {}", userUuid, e);
//            throw e;
//        }
//    }
//
//    @Operation(
//            summary = "유저가 환불 내역을 조회",
//            description = "유저가 환불된 내역을 조회",
//            tags = {"결제관리"}
//    )
//    @PreAuthorize("@paymentSecurityService.canAccessRefund(#refundUuid, authentication.principal)")
//    @GetMapping("/refund/{refundUuid}")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "출금 조회 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
//            @ApiResponse(responseCode = "404", description = "월렛을 찾을 수 없음"),
//            @ApiResponse(responseCode = "500", description = "서버오류")
//    })
//    public ResponseEntity<BasicResponseDto<RefundResponseDto>> getRefundRequestInfo(
//            Authentication authentication,
//            @PathVariable UUID refundUuid
//    ) {
//        UUID userUuid = getUserUuid(authentication);
//        log.info("Refund request retrieved user: {}", userUuid);
//
//        try {
//            RefundResponseDto response = paymentUseCase.getRefundInfo(refundUuid, userUuid);
//
//            return createSuccessResponse(REFUND_RETRIEVED_SUCCESS, response);
//        } catch (Exception e) {
//            log.error("Failed to process refund request - user: {}", userUuid, e);
//            throw e;
//        }
//    }
//
//
//    @Operation(
//            summary = "환불 내역 전체 조회",
//            description = "환불 출금 내역을 보기 위해 조회합니다.",
//            tags = {"결제관리"}
//    )
//    @PreAuthorize("@paymentSecurityService.canAccessRequest(#requestUuid, authentication.principal)")
//    @GetMapping("/refund/list")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "입금 요청 내역 조회 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
//            @ApiResponse(responseCode = "404", description = "입금 내역을 찾을 수 없음"),
//            @ApiResponse(responseCode = "500", description = "서버오류")
//    })
//    public ResponseEntity<BasicResponseDto<Page<RefundResponseDto>>> getRefundDetails(
//            @PageableDefault(size = 20, page = 0) Pageable pageable,
//            Authentication authentication
//    ) {
//
//        UUID userUuid = getUserUuid(authentication);
//        log.info("refund request retrieved  - user: {}", userUuid);
//
//        try {
//            Page<RefundResponseDto> response = paymentUseCase.getRefunds(userUuid, pageable);
//
//            return createSuccessResponse(REFUND_RETRIEVED_SUCCESS, response);
//        } catch (Exception e) {
//            log.error("Failed to process deposit request - admin: {}", userUuid, e);
//            throw e;
//        }
//    }
//
//
//    // -- helper method
//
//    private static UUID getUserUuid(Authentication authentication) {
//        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
//        return customUserDetails.getUserUuid();
//    }
//
//    private <T> ResponseEntity<BasicResponseDto<T>> createSuccessResponse(
//            String message, T data) {
//        return ResponseEntity.ok(BasicResponseDto.success(message, data));
//    }
//
//    private <T> ResponseEntity<BasicResponseDto<T>> createCreatedResponse(
//            String message,
//            T data,
//            String path,
//            Object... pathVariables) {
//        URI location = ServletUriComponentsBuilder
//                .fromCurrentRequest()
//                .path(path)
//                .buildAndExpand(pathVariables)
//                .toUri();
//        return ResponseEntity.created(location)
//                .body(BasicResponseDto.success(message, data));
//    }
//}
