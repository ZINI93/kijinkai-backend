package com.kijinkai.domain.orderitem.controller;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.orderitem.dto.OrderItemCountResponseDto;
import com.kijinkai.domain.orderitem.dto.OrderItemResponseDto;
import com.kijinkai.domain.orderitem.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.service.OrderItemService;
import com.kijinkai.domain.user.service.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "api/v1/order-items",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class OrderItemApiController {

    private final OrderItemService orderItemService;


    @GetMapping("/list")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 상품 전체 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<OrderItemResponseDto>>> getOrderItems(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        UUID user = getUserUuid(authentication);
        Page<OrderItemResponseDto> orderItems = orderItemService.getOrderItems(user, pageable);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved platform information", orderItems));
    }

    @GetMapping("/dashboard/count")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 상품 전체 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<OrderItemCountResponseDto>> getOrderItemDashboardCount(
            Authentication authentication
    ) {
        UUID user = getUserUuid(authentication);
        OrderItemCountResponseDto response = orderItemService.orderItemDashboardCount(user);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved platform information", response));
    }


    @GetMapping("/list/pending")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문상품 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "상품 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<OrderItemResponseDto>>> getOrderItemByPending(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        UUID user = getUserUuid(authentication);
        Page<OrderItemResponseDto> orderItems = orderItemService.getOrderItemByStatus(user, OrderItemStatus.PENDING, pageable);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved platform information", orderItems));
    }

    @GetMapping("/list/completed-product-payment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문상품 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "상품 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<OrderItemResponseDto>>> getOrderItemByProductPayment(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        UUID user = getUserUuid(authentication);
        Page<OrderItemResponseDto> orderItems = orderItemService.getOrderItemByStatus(user, OrderItemStatus.PRODUCT_PAYMENT_COMPLETED, pageable);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved platform information", orderItems));
    }

    @GetMapping("/list/delivery-payment-request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문상품 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "상품 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<OrderItemResponseDto>>> getOrderItemByDeliveryFeePaymentRequest(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        UUID user = getUserUuid(authentication);
        Page<OrderItemResponseDto> orderItems = orderItemService.getOrderItemByStatus(user, OrderItemStatus.DELIVERY_FEE_PAYMENT_REQUEST, pageable);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved platform information", orderItems));
    }

    @GetMapping("/list/approve")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문상품 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "상품 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<OrderItemResponseDto>>> getOrderItemByApprove(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        UUID user = getUserUuid(authentication);
        Page<OrderItemResponseDto> orderItems = orderItemService.getOrderItemByStatus(user, OrderItemStatus.PENDING_APPROVAL, pageable);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved platform information", orderItems));
    }

    @DeleteMapping("{orderUuid}")
    @Operation(summary = "delete order item", description = "delete order item")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful order item delete"),
            @ApiResponse(responseCode = "404", description = "Failed order item delete"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public ResponseEntity<Void> deleteUser(
            Authentication authentication,
            @PathVariable UUID orderUuid
    ) {
        UUID user = getUserUuid(authentication);
        orderItemService.deleteOrderItem(orderUuid);
        return ResponseEntity.noContent().build();
    }


    private static UUID getUserUuid(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUserUuid();
    }
}
