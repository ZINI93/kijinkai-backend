package com.kijinkai.domain.orderitem.adapter.in.web;

import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.orderitem.application.dto.OrderItemApprovalRequestDto;
import com.kijinkai.domain.orderitem.application.port.in.CreateOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.port.in.DeleteOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.port.in.GetOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.port.in.UpdateOrderItemUseCase;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/admin/order-items",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class OrderItemAdminApiController {

    private final CreateOrderItemUseCase createOrderItemUseCase;
    private final GetOrderItemUseCase getOrderItemUseCase;
    private final UpdateOrderItemUseCase updateOrderItemUseCase;
    private final DeleteOrderItemUseCase deleteOrderItemUseCase;


    @PostMapping(value = "/first-approve", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 승인 처리 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<List<String>>> firstApproveOrderItem(
            @Valid @RequestBody OrderItemApprovalRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        List<String> response = updateOrderItemUseCase.processFirstOderItem(customUserDetails.getUserUuid(), requestDto);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully approve order item", response));
    }


    @PostMapping(value = "/completed-local-delivery", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 국내배송 도착 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<List<String>>> completedLocalDelivery(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody OrderItemApprovalRequestDto requestDto
    ) {
        List<String> response = updateOrderItemUseCase.completeLocalDelivery(customUserDetails.getUserUuid(), requestDto);


        return ResponseEntity.ok(BasicResponseDto.success("Successfully complete local delivery", response));
    }

}
