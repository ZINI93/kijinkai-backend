package com.kijinkai.domain.shipment.controller;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.shipment.dto.ShipmentRequestDto;
import com.kijinkai.domain.shipment.dto.StartShipmentRequestDto;
import com.kijinkai.domain.shipment.service.ShipmentService;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
        value = "/api/v1/admin/shipment",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ShipmentAdminApiController {

    private final ShipmentService shipmentService;


    @PostMapping(value = "/{deliveryUuid}/create-shipment")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "박스 추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력값 (Validation 에러)"), // 404 대신 400 권장
            @ApiResponse(responseCode = "404", description = "박스를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BasicResponseDto<List<UUID>>> createShipment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable UUID deliveryUuid,
            @Valid @RequestBody ShipmentRequestDto requestDto
            ){

        List<UUID> shipments = shipmentService.createDeliveryBox(customUserDetails.getUserUuid(), deliveryUuid, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(BasicResponseDto.success("Successfully created shipment", shipments));
    }

    @PutMapping(value = "/{boxCode}/start")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "박스 추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력값 (Validation 에러)"), // 404 대신 400 권장
            @ApiResponse(responseCode = "404", description = "박스를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BasicResponseDto<String>> startShipment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String boxCode,
            @Valid @RequestBody StartShipmentRequestDto requestDto
            ){

        String shipment = shipmentService.registerTrackingNumber(customUserDetails.getUserUuid(), boxCode, requestDto);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully updated shipment", shipment));
    }



}



