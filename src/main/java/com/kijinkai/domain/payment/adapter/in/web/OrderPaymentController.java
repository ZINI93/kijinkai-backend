package com.kijinkai.domain.payment.adapter.in.web;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentDeliveryRequestDto;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;
import com.kijinkai.domain.payment.application.dto.response.OrderPaymentResponseDto;
import com.kijinkai.domain.payment.application.port.in.orderPayment.CreateOrderPaymentUseCase;
import com.kijinkai.domain.payment.application.port.in.orderPayment.DeleteOrderPaymentUseCase;
import com.kijinkai.domain.payment.application.port.in.orderPayment.GetOrderPaymentUseCase;
import com.kijinkai.domain.payment.application.port.in.orderPayment.UpdateOrderPaymentUseCase;
import com.kijinkai.domain.payment.domain.enums.OrderPaymentStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.awt.*;
import java.net.URI;
import java.util.UUID;

import static com.kijinkai.domain.payment.adapter.in.messaging.PaymentMassage.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/payments",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class OrderPaymentController {

    private final CreateOrderPaymentUseCase createOrderPaymentUseCase;
    private final GetOrderPaymentUseCase getOrderPaymentUseCase;
    private final UpdateOrderPaymentUseCase updateOrderPaymentUseCase;
    private final DeleteOrderPaymentUseCase deleteOrderPaymentUseCase;

    @Operation(
            summary = "상품에 대한 결제",
            description = "유저 - 상품에 대한 결제",
            tags = {"결제관리"}
    )
    @PostMapping("/first-payment")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "상품 결제 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "상품 주문 결제 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<OrderPaymentResponseDto>> completeFirstPayment(
            Authentication authentication,
            @Valid @RequestBody OrderPaymentRequestDto orderPaymentRequestDto
    ) {

        UUID userUuid = getUserUuid(authentication);
        log.info("Order payment first request create - User: {}", userUuid);

        try {
            OrderPaymentResponseDto response = updateOrderPaymentUseCase.completeFirstPayment(userUuid, orderPaymentRequestDto);
            return createSuccessResponse(ORDER_PAYMENT_COMPLETE_SUCCESS, response);
        } catch (Exception e) {
            log.error("Failed to process first order payment request - admin:{}", userUuid, e);
            throw e;
        }
    }


    @Operation(
            summary = "배송비 결제",
            description = "유저가 배송비 지불",
            tags = {"결제관리"}
    )
    @PostMapping("/delivery-fee")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "결제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "결제를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<OrderPaymentResponseDto>> createSecondPayment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody OrderPaymentDeliveryRequestDto requestDto
    ) {

        OrderPaymentResponseDto orderPayments = createOrderPaymentUseCase.paymentDeliverFee(customUserDetails.getUserUuid(), requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(BasicResponseDto.success("Successfully created delivery payment", orderPayments));
    }


    @Operation(
            summary = "유저 - 배송비에 대한 결제",
            description = "유저가 배송비에 대한 결제를 지불하기 위한 api",
            tags = {"결제관리"}
    )
    @PostMapping("/second-payment/complete")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "배송비 결제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "배송비 결제 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<OrderPaymentResponseDto>> completeSecondPayment(
            Authentication authentication,
            @Valid @RequestBody OrderPaymentDeliveryRequestDto orderPaymentDeliveryRequestDto

    ) {
        UUID userUuid = getUserUuid(authentication);

        try {
            OrderPaymentResponseDto response = createOrderPaymentUseCase.paymentDeliverFee(userUuid, orderPaymentDeliveryRequestDto);
            return createSuccessResponse(ORDER_PAYMENT_COMPLETE_SUCCESS, response);
        } catch (Exception e) {
            log.error("Failed to second order payment request - User: {}", userUuid);
            throw e;
        }
    }


    @Operation(
            summary = "관리자가 거래 내역을 조회",
            description = "관리자가 유저의 거래 내역을 조회합니다.",
            tags = {"결제관리"}
    )
    @GetMapping("/admin/order-payment/{requestUuid}")
    @PreAuthorize("hasRole('Admin')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거래 내역 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "거래 내역을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<OrderPaymentResponseDto>> getOrderPaymentInfoByAdmin(
            Authentication authentication,
            @PathVariable UUID paymentUuid
    ) {

        UUID userUuid = getUserUuid(authentication);

        try {
            OrderPaymentResponseDto response = getOrderPaymentUseCase.getOrderPaymentInfoByAdmin(userUuid, paymentUuid);
            return createSuccessResponse(ORDER_PAYMENT_RETRIEVED_SUCCESS, response);
        } catch (Exception e) {
            log.error("Failed to process order payment request - admin: {}", userUuid, e);
            throw e;
        }
    }

    @Operation(
            summary = "유저 - 배송비에 대한 결제",
            description = "유저가 배송비에 대한 결제를 지불하기 위한 api",
            tags = {"결제관리"}
    )
    @GetMapping("/order-payment/{requestUuid}")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "배송비 결제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "배송비 결제 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<OrderPaymentResponseDto>> getOrderPaymentInfo(
            Authentication authentication,
            @PathVariable UUID paymentUuid
    ) {
        UUID userUuid = getUserUuid(authentication);

        try {
            OrderPaymentResponseDto response = getOrderPaymentUseCase.getOrderPaymentInfo(userUuid, paymentUuid);
            return createSuccessResponse(ORDER_PAYMENT_RETRIEVED_SUCCESS, response);
        } catch (Exception e) {
            log.error("Failed to process order payment request - admin: {}", userUuid, e);
            throw e;
        }
    }


    @Operation(
            summary = "배송비 결제 대기중 리스트",
            description = "유저 - 배송비 결제 대기 리스트 api",
            tags = {"결제관리"}
    )
    @GetMapping("/list/shipping-payment-pending")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "배송비 결제 대기 리스트 호출 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "배송비 결제 대기 리스트 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<OrderPaymentResponseDto>>> getOrderPaymentsByPendingAndShipping(
            Authentication authentication,
            @PageableDefault(size = 20, page = 0) Pageable pageable
    ) {

        UUID userUuid = getUserUuid(authentication);

        try {
            Page<OrderPaymentResponseDto> response = getOrderPaymentUseCase.getOrderPaymentsByStatusAndType(userUuid, OrderPaymentStatus.PENDING, PaymentType.SHIPPING_PAYMENT, pageable);
            return createSuccessResponse(ORDER_PAYMENT_RETRIEVED_SUCCESS, response);
        } catch (Exception e) {
            log.error("Failed to process order payment - user: {}", userUuid);
            throw e;
        }
    }

    @GetMapping("/list/shipping-payment-complete")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "배송비 결제 대기 리스트 호출 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "배송비 결제 대기 리스트 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<OrderPaymentResponseDto>>> getOrderPaymentsByCompletedAndShipping(
            Authentication authentication,
            @PageableDefault(size = 20, page = 0) Pageable pageable
    ) {

        UUID userUuid = getUserUuid(authentication);

        try {
            Page<OrderPaymentResponseDto> response = getOrderPaymentUseCase.getOrderPaymentsByStatusAndType(userUuid, OrderPaymentStatus.COMPLETED, PaymentType.SHIPPING_PAYMENT, pageable);
            return createSuccessResponse(ORDER_PAYMENT_RETRIEVED_SUCCESS, response);
        } catch (Exception e) {
            log.error("Failed to process order payment - user: {}", userUuid);
            throw e;
        }
    }


    @Operation(
            summary = "본인 거래내역 조회",
            description = "유저가 본인 거래내역을 조회하기 위한 api",
            tags = {"결제관리"}
    )
    @GetMapping("/order-payments/list")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "배송비 결제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "배송비 결제 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<OrderPaymentResponseDto>>> getOrderPayments(
            Authentication authentication,
            @PageableDefault(size = 20, page = 0) Pageable pageable
    ) {

        UUID userUuid = getUserUuid(authentication);

        try {
            Page<OrderPaymentResponseDto> response = getOrderPaymentUseCase.getOrderPayments(userUuid, pageable);
            return createSuccessResponse(ORDER_PAYMENT_RETRIEVED_SUCCESS, response);
        } catch (Exception e) {
            log.error("Failed to process order payment - admin: {}", userUuid);
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
