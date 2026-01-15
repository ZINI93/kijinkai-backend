package com.kijinkai.domain.orderitem.adapter.in.web;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.orderitem.application.dto.OrderItemCountResponseDto;
import com.kijinkai.domain.orderitem.application.dto.OrderItemRequestDto;
import com.kijinkai.domain.orderitem.application.dto.OrderItemResponseDto;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.application.port.in.CreateOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.port.in.DeleteOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.port.in.GetOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.port.in.UpdateOrderItemUseCase;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/order-items",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class OrderItemApiController {

    private final CreateOrderItemUseCase createOrderItemUseCase;
    private final GetOrderItemUseCase getOrderItemUseCase;
    private final UpdateOrderItemUseCase updateOrderItemUseCase;
    private final DeleteOrderItemUseCase deleteOrderItemUseCase;


    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력값 (Validation 에러)"), // 404 대신 400 권장
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BasicResponseDto<List<UUID>>> createOrderItems(
            @Valid @RequestBody List<OrderItemRequestDto> orderItemRequestDtos,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        UUID userUuid = customUserDetails.getUserUuid();
        List<UUID> response = createOrderItemUseCase.createOrderItems(userUuid, orderItemRequestDtos);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully add order items ", response));
    }

    // 업데이트


    // ---- 조회. ----

    /**
     * 구매요청 리스트
     *
     * @param customUserDetails
     * @param pageable
     * @return
     */
    @GetMapping("/list/approve")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문상품 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "상품 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<OrderItemResponseDto>>> getOrderItemByApprove(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<OrderItemResponseDto> response = getOrderItemUseCase.getOrderItemByStatus(customUserDetails.getUserUuid(), OrderItemStatus.PENDING_APPROVAL, pageable);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved platform information", response));
    }


    /**
     * 1차 결제 완료 리스트
     *
     * @param customUserDetails
     * @param pageable
     * @return
     */
    @GetMapping("/list/completed-product-payment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문상품 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "상품 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<OrderItemResponseDto>>> getOrderItemByProductPayment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<OrderItemResponseDto> orderItems = getOrderItemUseCase.getOrderItemByStatus(customUserDetails.getUserUuid(), OrderItemStatus.PRODUCT_PAYMENT_COMPLETED, pageable);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully completed product payment", orderItems));
    }


    /**
     * 국내 배송완료 리스트
     * @param customUserDetails
     * @param pageable
     * @return
     */
    @GetMapping("/list/completed-local-delivery")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문상품 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "상품 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<OrderItemResponseDto>>> getLocalDeliveredList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<OrderItemResponseDto> orderItems = getOrderItemUseCase.getOrderItemByStatus(customUserDetails.getUserUuid(), OrderItemStatus.LOCAL_DELIVERY_COMPLETED, pageable);


        return ResponseEntity.ok(BasicResponseDto.success("Successfully delivered orderItem list", orderItems));
    }


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
        Page<OrderItemResponseDto> orderItems = getOrderItemUseCase.getOrderItems(user, pageable);

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
        OrderItemCountResponseDto response = getOrderItemUseCase.orderItemDashboardCount(user);

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
        Page<OrderItemResponseDto> orderItems = getOrderItemUseCase.getOrderItemByStatus(user, OrderItemStatus.PENDING, pageable);

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
        Page<OrderItemResponseDto> orderItems = getOrderItemUseCase.getOrderItemByStatus(user, OrderItemStatus.LOCAL_DELIVERY_COMPLETED, pageable);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved platform information", orderItems));
    }


    // ----- 삭제 -----

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
        deleteOrderItemUseCase.deleteOrderItem(orderUuid);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{orderItemCode}")
    @Operation(summary = "delete order item", description = "delete order item")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful order item delete"),
            @ApiResponse(responseCode = "404", description = "Failed order item delete"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public ResponseEntity<Void> deleteByOrderItemCode(
            @PathVariable(name = "orderItemCode") @NotBlank String orderItemCode,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        log.info("[OrderDelete] Request delete - User: {}, Code: {}", customUserDetails.getUserUuid(), orderItemCode);

        deleteOrderItemUseCase.deleteByOrderItemCode(customUserDetails.getUserUuid(), orderItemCode);

        return ResponseEntity.noContent().build();
    }


    private static UUID getUserUuid(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUserUuid();
    }
}
