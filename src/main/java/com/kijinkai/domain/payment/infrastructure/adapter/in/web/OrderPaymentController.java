package com.kijinkai.domain.payment.infrastructure.adapter.in.web;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;
import com.kijinkai.domain.payment.application.dto.response.DepositRequestResponseDto;
import com.kijinkai.domain.payment.application.dto.response.OrderPaymentResponseDto;
import com.kijinkai.domain.payment.application.port.in.PaymentUseCase;
import com.kijinkai.domain.user.service.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ConcurrentModificationException;
import java.util.UUID;

import static com.kijinkai.domain.payment.infrastructure.adapter.in.messaging.PaymentMassage.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/payments",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class OrderPaymentController {

    private final PaymentUseCase paymentUseCase;

    // ----  1차 결제   ----

    @Operation(
            summary = "1차 결제요청 생성",
            description = "관리자가 상품에 대한 1차 결제 요청을 생성 합니다.",
            tags = {"결제관리"}
    )
    @PostMapping("/admin/first-payment/{orderUuid}")
    @PreAuthorize("hasRole('Admin')")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "결제 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<OrderPaymentResponseDto>> createFirstOrderPayment(
            Authentication authentication,
            @PathVariable UUID orderUuid
    ) {
        UUID adminUuid = getUserUuid(authentication);
        log.info("First order payment created - Admin: {}, Order: {}", adminUuid, orderUuid);

        try {
            OrderPaymentResponseDto response = paymentUseCase.createFirstPayment(adminUuid, orderUuid);
            return createCreatedResponse(ORDER_PAYMENT_CREATE_SUCCESS, response,
                    "/api/v1/payments/order-payment/{paymentUuid}", response.getPaymentUuid());
        } catch (Exception e) {
            log.error("Failed to create first order payment - admin: {}, order: {}", adminUuid, orderUuid, e);
            throw e;
        }
    }

    @Operation(
            summary = "1차 결제 지불",
            description = "관리자가 생성한 1차 결제 요청서를 유저가 지불합니다",
            tags = {"결제관리"}
    )
    @PutMapping("/first-payment/{paymentUuid}/complete")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 요청 처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "결제내역을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "동시 처리 중"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<OrderPaymentResponseDto>> completeFirstOrderPayment(
            Authentication authentication,
            @PathVariable UUID paymentUuid
    ) {
        UUID userUuid = getUserUuid(authentication);
        log.info("Completing first order payment - User: {}, Payment: {}", userUuid, paymentUuid);

        try {
            OrderPaymentResponseDto response = paymentUseCase.completeFirstPayment(userUuid, paymentUuid);
            return createSuccessResponse(ORDER_PAYMENT_COMPLETE_SUCCESS, response);
        } catch (Exception e) {
            log.error("Failed to complete first order payment - user: {}, payment: {}", userUuid, paymentUuid, e);
            throw e;
        }
    }

    // ----  2차 결제   ----

    @Operation(
            summary = "2차 결제요청 생성",
            description = "관리자가 배송비에 대한 2차 결제 요청을 생성 합니다.",
            tags = {"결제관리"}
    )
    @PostMapping("/admin/second-payment/{orderUuid}")
    @PreAuthorize("hasRole('Admin')")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "결제 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<OrderPaymentResponseDto>> createSecondOrderPayment(
            Authentication authentication,
            @PathVariable UUID orderUuid,
            @RequestBody OrderPaymentRequestDto requestDto
    ) {
        UUID adminUuid = getUserUuid(authentication);
        log.info("Second order payment created - Admin: {}, Order: {}", adminUuid, orderUuid);

        try {
            OrderPaymentResponseDto response = paymentUseCase.createSecondPayment(adminUuid, orderUuid, requestDto);
            return createCreatedResponse(ORDER_PAYMENT_CREATE_SUCCESS, response,
                    "/api/v1/payments/order-payment/{paymentUuid}", response.getPaymentUuid());
        } catch (Exception e) {
            log.error("Failed to create second order payment - admin: {}, order: {}", adminUuid, orderUuid, e);
            throw e;
        }
    }

    @Operation(
            summary = "2차 결제 지불",
            description = "관리자가 생성한 2차 결제 요청서를 유저가 지불합니다",
            tags = {"결제관리"}
    )
    @PutMapping("/second-payment/{paymentUuid}/complete")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 요청 처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "결제내역을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "동시 처리 중"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<OrderPaymentResponseDto>> completeSecondOrderPayment(
            Authentication authentication,
            @PathVariable UUID paymentUuid
    ) {
        UUID userUuid = getUserUuid(authentication);
        log.info("Completing second order payment - User: {}, Payment: {}", userUuid, paymentUuid);

        try {
            OrderPaymentResponseDto response = paymentUseCase.completeSecondPayment(userUuid, paymentUuid);
            return createSuccessResponse(ORDER_PAYMENT_COMPLETE_SUCCESS, response);
        } catch (Exception e) {
            log.error("Failed to complete second order payment - user: {}, payment: {}", userUuid, paymentUuid, e);
            throw e;
        }
    }

    // -- 조회

    @Operation(
            summary = "관리자가 유저의 거래내역을 조회",
            description = "관리를 위해서 관리자가 유저 거래내역을 조회합니다.",
            tags = {"결제관리"}
    )
    @GetMapping("/admin/order-payment/{paymentUuid}")
    @PreAuthorize("hasRole('Admin')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거래 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "결제 내역을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<OrderPaymentResponseDto>> getOrderPaymentInfoByAdmin(
            Authentication authentication,
            @PathVariable UUID paymentUuid
    ) {
        UUID adminUuid = getUserUuid(authentication);
        log.info("Order payment retrieved by admin - Admin: {}, Payment: {}", adminUuid, paymentUuid);

        try {
            OrderPaymentResponseDto response = paymentUseCase.getOrderPaymentInfoByAdmin(paymentUuid, adminUuid);
            return createSuccessResponse(ORDER_PAYMENT_RETRIEVED_SUCCESS, response);
        } catch (Exception e) {
            log.error("Failed to retrieve order payment info - admin: {}, payment: {}", adminUuid, paymentUuid, e);
            throw e;
        }
    }

    @Operation(
            summary = "유저가 본인 거래 내역을 조회",
            description = "유저 본인 거래 내역을 조회합니다.",
            tags = {"결제관리"}
    )
    @GetMapping("/order-payment/{paymentUuid}")
    @PreAuthorize("@paymentSecurityService.canAccessOrderPayment(#paymentUuid, authentication.principal)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거래 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "거래 내역을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<OrderPaymentResponseDto>> getOrderPaymentInfo(
            Authentication authentication,
            @PathVariable UUID paymentUuid
    ) {
        UUID userUuid = getUserUuid(authentication);
        log.info("Order payment retrieved - User: {}, Payment: {}", userUuid, paymentUuid);

        try {
            OrderPaymentResponseDto response = paymentUseCase.getOrderPaymentInfo(paymentUuid, userUuid);
            return createSuccessResponse(ORDER_PAYMENT_RETRIEVED_SUCCESS, response);
        } catch (Exception e) {
            log.error("Failed to retrieve order payment info - user: {}, payment: {}", userUuid, paymentUuid, e);
            throw e;
        }
    }

    // -- Helper Methods

    private static UUID getUserUuid(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUserUuid();
    }

    private <T> ResponseEntity<BasicResponseDto<T>> createSuccessResponse(String message, T data) {
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

