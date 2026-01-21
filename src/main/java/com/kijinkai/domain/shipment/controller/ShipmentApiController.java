package com.kijinkai.domain.shipment.controller;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.shipment.dto.ShipmentBoxItemResponseDto;
import com.kijinkai.domain.shipment.dto.ShipmentResponseDto;
import com.kijinkai.domain.shipment.entity.ShipmentStatus;
import com.kijinkai.domain.shipment.service.ShipmentService;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/shipment",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ShipmentApiController {

    private final ShipmentService shipmentService;



    @PutMapping(value = "/{boxCode}/delivered")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "배송 완료 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력값 (Validation 에러)"), // 404 대신 400 권장
            @ApiResponse(responseCode = "404", description = "박스를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BasicResponseDto<String>> delivered(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String boxCode
    ){
        String delivered = shipmentService.delivered(customUserDetails.getUserUuid(), boxCode, ShipmentStatus.SHIPPED);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully delivered box", delivered));
    }


    // 조회

    @GetMapping(value ="/pending")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "박스 찾기 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력값 (Validation 에러)"), // 404 대신 400 권장
            @ApiResponse(responseCode = "404", description = "박스를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BasicResponseDto<Page<ShipmentResponseDto>>> getShipmentByPending(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable
            ){

        Page<ShipmentResponseDto> shipmentsByPending = shipmentService.getShipmentsByStatus(customUserDetails.getUserUuid(), ShipmentStatus.PAYMENT_PENDING, pageable);


        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved shipments", shipmentsByPending));
    }

    @GetMapping(value ="/preparing")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "박스 찾기 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력값 (Validation 에러)"), // 404 대신 400 권장
            @ApiResponse(responseCode = "404", description = "박스를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BasicResponseDto<Page<ShipmentResponseDto>>> getShipmentByPreparing(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable
            ){

        Page<ShipmentResponseDto> shipmentsByPending = shipmentService.getShipmentsByStatus(customUserDetails.getUserUuid(), ShipmentStatus.PREPARING, pageable);


        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved shipments", shipmentsByPending));
    }

    @GetMapping(value ="/shipped")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "박스 찾기 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력값 (Validation 에러)"), // 404 대신 400 권장
            @ApiResponse(responseCode = "404", description = "박스를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BasicResponseDto<Page<ShipmentResponseDto>>> getShipped(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable
            ){

        Page<ShipmentResponseDto> shipmentsByShipped= shipmentService.getShipmentsByStatus(customUserDetails.getUserUuid(), ShipmentStatus.SHIPPED, pageable);


        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved shipments", shipmentsByShipped));
    }

    @GetMapping(value ="/delivered")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "박스 찾기 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력값 (Validation 에러)"), // 404 대신 400 권장
            @ApiResponse(responseCode = "404", description = "박스를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BasicResponseDto<Page<ShipmentResponseDto>>> getDelivered(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable
            ){

        Page<ShipmentResponseDto> shipmentsByDelivered = shipmentService.getShipmentsByStatus(customUserDetails.getUserUuid(), ShipmentStatus.DELIVERED, pageable);


        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved shipments", shipmentsByDelivered));
    }

    @GetMapping(value ="/pending/{boxCode}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "박스 상품 찾기 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력값 (Validation 에러)"), // 404 대신 400 권장
            @ApiResponse(responseCode = "404", description = "박스를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BasicResponseDto<List<ShipmentBoxItemResponseDto>>> getShipmentBoxItems(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String boxCode
    ){

        List<ShipmentBoxItemResponseDto> boxItems = shipmentService.getBoxItems(customUserDetails.getUserUuid(), boxCode);


        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved shipments", boxItems));
    }
}
