package com.kijinkai.domain.order.adapter.in.web;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.order.application.dto.OrderRequestDto;
import com.kijinkai.domain.order.application.dto.OrderResponseDto;
import com.kijinkai.domain.order.application.port.in.OrderFacadeUseCase;
import com.kijinkai.domain.order.application.service.OrderApplicationService;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    private final OrderFacadeUseCase orderFacadeUseCase;


    @PostMapping(value = "/first-order/complete", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "주문 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "주문를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<OrderResponseDto>> createOrder(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody OrderRequestDto requestDto
    ) {


        OrderResponseDto order = orderFacadeUseCase.completedOrder(customUserDetails.getUserUuid(), requestDto);

        log.info("Order successfully created. OrderUuid: {}, UserUuid: {}",
                order.getOrderUuid(), customUserDetails.getUserUuid());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{orderUuid}")
                .buildAndExpand(order.getOrderUuid())
                .toUri();

        return ResponseEntity.created(location).body(BasicResponseDto.success("Successful created address", order));
    }
}

