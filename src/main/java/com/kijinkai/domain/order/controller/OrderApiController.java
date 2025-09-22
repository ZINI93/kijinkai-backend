package com.kijinkai.domain.order.controller;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.order.dto.OrderRequestDto;
import com.kijinkai.domain.order.dto.OrderResponseDto;
import com.kijinkai.domain.order.service.OrderService;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(
        value = "/api/v1/orders",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class OrderApiController {

    private final OrderService orderService;

    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "주문 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "주문를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<OrderResponseDto>> createOrder(
            Authentication authentication,
            @Valid @RequestBody OrderRequestDto requestDto
    ) {

        UUID userUuid = getUserUuid(authentication);
        log.info("User: {} requests order create", userUuid);

        OrderResponseDto order = orderService.createOrderProcess(userUuid, requestDto);
        log.info("Order:{} successfully created by user{}", order.getOrderUuid(), userUuid);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/api/v1/orders/{orderUuid}")
                .buildAndExpand(order.getOrderUuid())
                .toUri();

        return ResponseEntity.created(location).body(BasicResponseDto.success("Successful created address", order));
    }

    private static UUID getUserUuid(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUserUuid();
    }

}

