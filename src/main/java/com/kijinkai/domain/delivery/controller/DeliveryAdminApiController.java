package com.kijinkai.domain.delivery.controller;

import com.kijinkai.domain.common.BaseController;
import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.delivery.dto.DeliveryRequestDto;
import com.kijinkai.domain.delivery.dto.DeliveryResponseDto;
import com.kijinkai.domain.delivery.dto.DeliveryUpdateDto;
import com.kijinkai.domain.delivery.service.DeliveryService;
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
@RestController
@RequestMapping(
        value = "/api/v1/deliveries",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class DeliveryAdminApiController extends BaseController {

    private final DeliveryService deliveryService;

    /**
     * 관리자에 의해서 배송작성
     */
    @PostMapping("/{orderUuid}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "관리자가 배송 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<DeliveryResponseDto>> createDelivery(
            Authentication authentication,
            @PathVariable UUID orderUuid,
            @Valid @RequestBody DeliveryRequestDto requestDto
    ) {
        UUID userUuid = getUserUuid(authentication);
        log.info("Creating delivery for order: {} by admin: {}", orderUuid, userUuid);

        try {
            DeliveryResponseDto delivery = deliveryService.createDeliveryWithValidate(userUuid, orderUuid, requestDto);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/v1/deliveries/{deliveryUuid}")
                    .buildAndExpand(delivery.getDeliveryUuid())
                    .toUri();

            return ResponseEntity.created(location)
                    .body(BasicResponseDto.success("배송이 성공적으로 등록되었습니다", delivery));
        } catch (Exception e) {
            log.error("Failed to create delivery for order: {} by admin: {}", orderUuid, userUuid, e);
            throw e;
        }
    }

    /**
     * 관리자에 의해서 배송시작
     */
    @PutMapping("/{deliveryUuid}/ship")
    @PreAuthorize("hasRole('ADMIN') and @securityService.canAccessDelivery(#deliveryUuid)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송 시작 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "배송을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "배송 시작할 수 없는 상태"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<DeliveryResponseDto>> deliveryShipped(
            Authentication authentication,
            @PathVariable UUID deliveryUuid
    ) {
        UUID userUuid = getUserUuid(authentication);
        log.info("Starting delivery: {} by admin: {}", deliveryUuid, userUuid);

        try {
            DeliveryResponseDto delivery = deliveryService.deliveryShipped(userUuid, deliveryUuid);
            return ResponseEntity.ok(BasicResponseDto.success("배송이 시작되었습니다", delivery));
        } catch (Exception e) {
            log.error("Failed to start delivery: {} by admin: {}", deliveryUuid, userUuid, e);
            throw e;
        }
    }

    /**
     * 배송시작 전 배송정보 업데이트
     */
    @PutMapping("/{deliveryUuid}")
    @PreAuthorize("hasRole('ADMIN') and @securityService.canAccessDelivery(#deliveryUuid)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송 정보 업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "배송을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "업데이트할 수 없는 상태"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<DeliveryResponseDto>> updateDelivery(
            Authentication authentication,
            @PathVariable UUID deliveryUuid,
            @Valid @RequestBody DeliveryUpdateDto updateDto
    ) {
        UUID userUuid = getUserUuid(authentication);
        log.info("Updating delivery: {} by admin: {}", deliveryUuid, userUuid);

        try {
            DeliveryResponseDto delivery = deliveryService.updateDeliveryWithValidate(userUuid, deliveryUuid, updateDto);
            return ResponseEntity.ok(BasicResponseDto.success("배송 정보가 업데이트되었습니다", delivery));
        } catch (Exception e) {
            log.error("Failed to update delivery: {} by admin: {}", deliveryUuid, userUuid, e);
            throw e;
        }
    }

    /**
     * 관리자에 의해서 배송삭제
     */
    @DeleteMapping("/{deliveryUuid}")
    @PreAuthorize("hasRole('ADMIN') and @securityService.canAccessDelivery(#deliveryUuid)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "배송 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "배송을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "삭제할 수 없는 상태"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<Void> deleteDelivery(
            Authentication authentication,
            @PathVariable UUID deliveryUuid
    ) {
        UUID userUuid = getUserUuid(authentication);
        log.info("Deleting delivery: {} by admin: {}", deliveryUuid, userUuid);

        try {
            deliveryService.deleteDelivery(userUuid, deliveryUuid);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete delivery: {} by admin: {}", deliveryUuid, userUuid, e);
            throw e;
        }
    }
}