package com.kijinkai.domain.shipment.controller;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.shipment.dto.shipment.request.ShipmentRequestDto;
import com.kijinkai.domain.shipment.dto.shipment.request.ShipmentTrackingRequestDto;
import com.kijinkai.domain.shipment.dto.shipment.request.ShipmentUpdateDto;
import com.kijinkai.domain.shipment.dto.shipment.response.ShipmentResponseDto;
import com.kijinkai.domain.shipment.dto.shipmentBoxItem.StartShipmentRequestDto;
import com.kijinkai.domain.shipment.service.ShipmentService;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
        value = "/api/v1/admin/shipments",
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
    ) {

        List<UUID> shipments = shipmentService.createDeliveryBox(customUserDetails.getUserUuid(), deliveryUuid, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(BasicResponseDto.success("Successfully created shipment", shipments));
    }


    @Operation(summary = "관리자용 배송 패키지 목록 조회", description = "관리자가 특정 배송건에 포함된 패키지(박스) 목록을 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ShipmentResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음 (관리자 전용)", content = @Content),
            @ApiResponse(responseCode = "404", description = "배송 또는 유저 정보를 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @GetMapping(value = "/delivery/{deliveryUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BasicResponseDto<ShipmentResponseDto>> getPackagesByAdmin(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID deliveryUuid,
            @PageableDefault(size = 10) Pageable pageable) {

        ShipmentResponseDto data = shipmentService.getPackagesByAdmin(
                userDetails.getUserUuid(),
                deliveryUuid,
                pageable
        );

        return ResponseEntity.ok(BasicResponseDto.success("배송 패키지 목록 조회가 완료되었습니다.", data));
    }


    @Operation(summary = "배송 패킹 취소", description = "관리자 권한으로 특정 배송건에 생성된 모든 패키지(박스) 정보를 삭제하고 배송 상태를 보류(Pending)로 되돌립니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "취소 성공: 패킹 데이터 삭제 및 상태 변경 완료", content = @Content),
            @ApiResponse(responseCode = "400", description = "잘못된 요청: 유효하지 않은 UUID 형식", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음: 관리자 권한이 없는 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "찾을 수 없음: 해당 배송건이 존재하지 않음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @DeleteMapping(value = "/{deliveryUuid}/packed", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BasicResponseDto<Void>> cancelPacked(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID deliveryUuid) {

        shipmentService.cancelPacked(userDetails.getUserUuid(), deliveryUuid);

        return ResponseEntity.ok(BasicResponseDto.success("배송 패킹 취소 및 상태 변경이 완료되었습니다.", null));
    }


    @Operation(summary = "배송 박스 정보 수정", description = "관리자 권한으로 특정 박스의 정보를 수정하고, 해당 배송건의 전체 배송비를 재계산합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공: 박스 정보 업데이트 및 배송비 재계산 완료",
                    content = @Content(schema = @Schema(implementation = UUID.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청: 유효하지 않은 데이터 또는 존재하지 않는 박스", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음: 관리자 권한이 없는 사용자", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @PutMapping(value = "/{shipmentUuid}/box-edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BasicResponseDto<ShipmentResponseDto>> updatePackedShipment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID shipmentUuid,
            @RequestBody ShipmentUpdateDto updateDto) {

        ShipmentResponseDto updatedShipmentUuid = shipmentService.updatePackedShipment(
                userDetails.getUserUuid(),
                shipmentUuid,
                updateDto
        );

        return ResponseEntity.ok(BasicResponseDto.success("배송 박스 정보 수정 및 배송비 재계산이 완료되었습니다.", updatedShipmentUuid));
    }

    @Operation(summary = "운송장 번호 추가 및 배송 상태 변경", description = "관리자 권한으로 특정 박스에 운송장 번호를 등록하고, 박스 및 전체 배송 상태를 '배송 중(Shipped)'으로 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "운송장 등록 및 배송 시작 처리 성공",
                    content = @Content(schema = @Schema(implementation = ShipmentResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청: 존재하지 않는 박스 또는 유효하지 않은 데이터", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음: 관리자 권한이 없는 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "찾을 수 없음: 해당 박스 정보를 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @PutMapping(value = "/{shipmentUuid}/tracking-number", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BasicResponseDto<ShipmentResponseDto>> addTrackingNo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID shipmentUuid,
            @RequestBody ShipmentTrackingRequestDto requestDto) {

        ShipmentResponseDto response = shipmentService.addTrackingNo(
                userDetails.getUserUuid(),
                shipmentUuid,
                requestDto
        );

        return ResponseEntity.ok(BasicResponseDto.success("운송장 번호 등록 및 배송 상태 업데이트가 완료되었습니다.", response));
    }

}



