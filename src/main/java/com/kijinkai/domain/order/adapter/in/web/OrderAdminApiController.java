package com.kijinkai.domain.order.adapter.in.web;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderStatus;
import com.kijinkai.domain.order.application.dto.OrderResponseDto;
import com.kijinkai.domain.order.application.port.in.GetOrderUseCase;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.application.dto.OrderItemResponseDto;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(
        value = "/api/v1/admin/orders",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class OrderAdminApiController {

    private final GetOrderUseCase getOrderUseCase;

    // -- 조회.
    @GetMapping
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "상품 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<OrderResponseDto>>> getOrderItems(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(required = false) String orderCode,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20, page = 0) Pageable pageable
    ){

        Page<OrderResponseDto> responses = getOrderUseCase.getOrdersByAdmin(customUserDetails.getUserUuid(), orderCode, name, status, startDate, endDate, pageable);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved order", responses));
    }
}
