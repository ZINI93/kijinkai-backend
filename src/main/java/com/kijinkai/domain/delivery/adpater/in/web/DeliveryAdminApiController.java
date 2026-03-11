package com.kijinkai.domain.delivery.adpater.in.web;

import com.kijinkai.domain.common.BaseController;
import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.delivery.application.dto.request.DeliveryCancelRequestDto;
import com.kijinkai.domain.delivery.application.dto.response.DeliveryCountResponseDto;
import com.kijinkai.domain.delivery.application.dto.request.DeliveryRequestDto;
import com.kijinkai.domain.delivery.application.dto.response.DeliveryResponseDto;
import com.kijinkai.domain.delivery.application.dto.request.DeliveryUpdateDto;
import com.kijinkai.domain.delivery.domain.model.DeliveryStatus;
import com.kijinkai.domain.delivery.application.in.CreateDeliveryUseCase;
import com.kijinkai.domain.delivery.application.in.DeleteDeliveryUseCase;
import com.kijinkai.domain.delivery.application.in.GetDeliveryUseCase;
import com.kijinkai.domain.delivery.application.in.UpdateDeliveryUseCase;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/admin/deliveries",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class DeliveryAdminApiController extends BaseController {

    private final CreateDeliveryUseCase createDeliveryUseCase;
    private final GetDeliveryUseCase getDeliveryUseCase;
    private final UpdateDeliveryUseCase updateDeliveryUseCase;
    private final DeleteDeliveryUseCase deleteDeliveryUseCase;

    /**
     * 관리자에 의해서 배송작성
     */
    @PostMapping("/{orderPaymentUuid}/create")
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
            @PathVariable("orderPaymentUuid") UUID orderPaymentUuid,
            @Valid @RequestBody DeliveryRequestDto requestDto
    ) {
        UUID userUuid = getUserUuid(authentication);
        log.info("Creating delivery for orderPayment: {} by admin: {}", orderPaymentUuid, userUuid);

        try {
            DeliveryResponseDto delivery = createDeliveryUseCase.createDelivery(userUuid, orderPaymentUuid, requestDto);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/v1/deliveries/{deliveryUuid}")
                    .buildAndExpand(delivery.getDeliveryUuid())
                    .toUri();

            return ResponseEntity.created(location)
                    .body(BasicResponseDto.success("배송이 성공적으로 등록되었습니다", delivery));
        } catch (Exception e) {
            log.error("Failed to create delivery for order: {} by admin: {}", orderPaymentUuid, userUuid, e);
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
            DeliveryResponseDto delivery = updateDeliveryUseCase.shipDelivery(userUuid, deliveryUuid);
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
            DeliveryResponseDto delivery = updateDeliveryUseCase.updateDeliveryWithValidate(userUuid, deliveryUuid, updateDto);
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
            deleteDeliveryUseCase.deleteDelivery(userUuid, deliveryUuid);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete delivery: {} by admin: {}", deliveryUuid, userUuid, e);
            throw e;
        }
    }


    @GetMapping("/list/shipping")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "배송을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<DeliveryResponseDto>>> getDeliveriesByShipping(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        UUID userUuid = getUserUuid(authentication);
        Page<DeliveryResponseDto> response = getDeliveryUseCase.getDeliveriesByStatus(userUuid, DeliveryStatus.SHIPPED, pageable);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved order payment information", response));
    }

    @GetMapping("/list/delivered")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "배송을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<DeliveryResponseDto>>> getDeliveriesByDelivered(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        UUID userUuid = getUserUuid(authentication);
        Page<DeliveryResponseDto> response = getDeliveryUseCase.getDeliveriesByStatus(userUuid, DeliveryStatus.DELIVERED, pageable);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved order payment information", response));
    }


    @GetMapping("/dashboard/statistics")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송 count 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "배송을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<DeliveryCountResponseDto>> getDeliveryDetailByAdmin(
            Authentication authentication
    ) {
        UUID userUuid = getUserUuid(authentication);
        DeliveryCountResponseDto response = getDeliveryUseCase.getDeliveryDashboardCount(userUuid);


        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved order payment information", response));
    }

    @GetMapping("/{deliveryUuid}/request-box")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "배송을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<DeliveryResponseDto>> getDeliveriesCountByStatus(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable UUID deliveryUuid,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {

        DeliveryResponseDto response = getDeliveryUseCase.getRequestDeliveryOrderItemByAdmin(customUserDetails.getUserUuid(), deliveryUuid, pageable);


        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved order payment information", response));
    }


    // 조회.
    @GetMapping("/request-pending")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송 count 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "배송을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<DeliveryResponseDto>>> getDeliveryByRequestPending(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {

        Page<DeliveryResponseDto> deliveriesByPending = getDeliveryUseCase.getDeliveriesByStatus(customUserDetails.getUserUuid(), DeliveryStatus.PENDING, pageable);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved deliveries", deliveriesByPending));
    }


    @Operation(summary = "배송비 결제 요청", description = "관리자 권한으로 포장된 박스들의 요금을 합산하여 고객에게 배송비 결제를 요청하고 결제 정보를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 요청 성공: 배송비 합산 및 결제 데이터 생성 완료",
                    content = @Content(schema = @Schema(implementation = DeliveryResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청: 포장된 박스가 없는 경우 등", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음: 관리자 권한이 없는 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "찾을 수 없음: 배송 또는 고객 정보가 존재하지 않음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @PostMapping(value = "/{deliveryUuid}/payment-request", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BasicResponseDto<DeliveryResponseDto>> requestDeliveryPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID deliveryUuid) {

        DeliveryResponseDto response = updateDeliveryUseCase.requestDeliveryPayment(
                userDetails.getUserUuid(),
                deliveryUuid
        );

        return ResponseEntity.ok(BasicResponseDto.success("배송비 결제 요청이 성공적으로 처리되었습니다.", response));
    }


    // -- 조회.
    @GetMapping
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "배송요청 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "배송을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<DeliveryResponseDto>>> getDeliveries(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) DeliveryStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20, page = 0) Pageable pageable
    ) {

        Page<DeliveryResponseDto> responses = getDeliveryUseCase.getDeliveriesByAdmin(customUserDetails.getUserUuid(), name, phoneNumber, status, startDate, endDate, pageable);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved delivery", responses));
    }
    @Operation(summary = "배송 취소 처리", description = "관리자 권한으로 배송을 취소하고 취소 사유를 기록하며, 연관된 모든 박스(Shipment) 데이터를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "취소 성공: 배송 상태 변경 및 관련 데이터 삭제 완료",
                    content = @Content(schema = @Schema(implementation = DeliveryResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청: 이미 취소되었거나 취소가 불가능한 상태", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음: 관리자 권한이 없는 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "찾을 수 없음: 해당 배송 정보를 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @PutMapping(value = "/{deliveryUuid}/cancel", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BasicResponseDto<DeliveryResponseDto>> cancelDelivery(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID deliveryUuid,
            @RequestBody DeliveryCancelRequestDto requestDto) {

        DeliveryResponseDto response = updateDeliveryUseCase.cancelDelivery(
                userDetails.getUserUuid(),
                deliveryUuid,
                requestDto
        );

        return ResponseEntity.ok(BasicResponseDto.success("배송 취소 처리가 완료되었습니다.", response));
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공: 취소 사유 정보 반환",
                    content = @Content(schema = @Schema(implementation = DeliveryResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청: 취소 상태가 아닌 배송 건 조회 시도", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음: 관리자 권한이 없는 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "찾을 수 없음: 해당 배송 정보를 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @GetMapping(value = "/{deliveryUuid}/cancel-reason", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BasicResponseDto<DeliveryResponseDto>> getCancelReason(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID deliveryUuid) {

        DeliveryResponseDto response = getDeliveryUseCase.getCancelReason(
                userDetails.getUserUuid(),
                deliveryUuid
        );

        return ResponseEntity.ok(BasicResponseDto.success("배송 취소 사유 조회가 완료되었습니다.", response));
    }

    @Operation(summary = "취소된 배송 복구", description = "관리자 권한으로 취소된 배송 건을 다시 '보류(Pending)' 상태로 복구합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "복구 성공: 배송 상태가 보류(Pending)로 변경됨",
                    content = @Content(schema = @Schema(implementation = DeliveryResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청: 복구가 불가능한 상태이거나 잘못된 식별자", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음: 관리자 권한이 없는 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "찾을 수 없음: 해당 배송 정보를 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @PutMapping(value = "/{deliveryUuid}/restore", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BasicResponseDto<DeliveryResponseDto>> restoreDelivery(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID deliveryUuid) {

        DeliveryResponseDto response = updateDeliveryUseCase.restoreDelivery(
                userDetails.getUserUuid(),
                deliveryUuid
        );

        return ResponseEntity.ok(BasicResponseDto.success("배송 복구 처리가 완료되었습니다.", response));
    }



}