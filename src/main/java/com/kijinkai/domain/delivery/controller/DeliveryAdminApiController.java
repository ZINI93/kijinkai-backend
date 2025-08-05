package com.kijinkai.domain.delivery.controller;

import com.kijinkai.domain.common.BaseController;
import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.delivery.dto.DeliveryRequestDto;
import com.kijinkai.domain.delivery.dto.DeliveryResponseDto;
import com.kijinkai.domain.delivery.dto.DeliveryUpdateDto;
import com.kijinkai.domain.delivery.service.DeliveryService;
import com.kijinkai.domain.user.service.CustomUserDetails;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') and @securityService.canAccessDelivery(#dliveryUuid)")
@RestController
@RequestMapping(
        value = "/api/v1/deliveries",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class DeliveryAdminApiController extends BaseController {

    private final DeliveryService deliveryService;


    /**
     * 관리자에 의해서 배송작성
     *
     * @param authentication
     * @param orderUuid
     * @param requestDto
     * @return
     */
    @PostMapping("/{orderUuid}")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "관리자가 배송 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "배송을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<DeliveryResponseDto>> createDelivery(
            Authentication authentication,
            @PathVariable UUID orderUuid,
            @Valid @RequestBody DeliveryRequestDto requestDto
    ) {
        UUID userUuid = getUserUuid(authentication);

        DeliveryResponseDto delivery = deliveryService.createDeliveryWithValidate(userUuid, orderUuid, requestDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/deliveries/{deliveryUuid}")
                .buildAndExpand(delivery.getDeliveryUuid())
                .toUri();

        return ResponseEntity.created(location).body(BasicResponseDto.success("Successful created delivery", delivery));
    }

    /**
     * 관리자에 의해서 배송시작
     *
     * @param authentication
     * @param deliveryUuid
     * @return
     */
    @PutMapping("/{deliveryUuid}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "관리자에 의해서 배송시작 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "배송을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<DeliveryResponseDto>> deliveryShipped(
            Authentication authentication,
            @PathVariable UUID deliveryUuid
    ) {
        UUID userUuid = getUserUuid(authentication);

        DeliveryResponseDto delivery = deliveryService.deliveryShipped(userUuid, deliveryUuid);

        return ResponseEntity.ok(BasicResponseDto.success("Successful ship delivery", delivery));
    }

    /**
     * 배송시작 전 배송정보 업데이트
     *
     * @param authentication
     * @param deliveryUuid
     * @param updateDto
     * @return
     */
    @PutMapping("/{deliveryUuid}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "관리자에 의해서 배송시작 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "배송을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<DeliveryResponseDto>> updateDelivery(
            Authentication authentication,
            @PathVariable UUID deliveryUuid,
            @Valid @RequestBody DeliveryUpdateDto updateDto
    ) {

        UUID userUuid = getUserUuid(authentication);
        DeliveryResponseDto delivery = deliveryService.updateDeliveryWithValidate(userUuid, deliveryUuid, updateDto);

        return ResponseEntity.ok(BasicResponseDto.success("Successful update delivery", delivery));
    }

    /**
     * 관리자에 의해서 배송삭제
     *
     * @param authentication
     * @param deliveryUuid
     * @return
     */
    @DeleteMapping("/{deliveryUuid}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "관리자에 의해서 배송 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "배송을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<Void> deleteDelivery(
            Authentication authentication,
            @PathVariable UUID deliveryUuid
    ) {

        UUID userUuid = getUserUuid(authentication);
        deliveryService.deleteDelivery(userUuid, deliveryUuid);

        return ResponseEntity.noContent().build();
    }


}
