package com.kijinkai.domain.orderitem.adapter.in.web;

import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.application.dto.ArrivedItemRequestDto;
import com.kijinkai.domain.orderitem.application.dto.OrderItemApprovalRequestDto;
import com.kijinkai.domain.orderitem.application.dto.OrderItemRejectRequestDto;
import com.kijinkai.domain.orderitem.application.dto.OrderItemResponseDto;
import com.kijinkai.domain.orderitem.application.port.in.CreateOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.port.in.DeleteOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.port.in.GetOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.port.in.UpdateOrderItemUseCase;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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

    @Operation(summary = "주문 상품 1차 승인 및 정보 업데이트", description = "관리자 권한으로 주문된 상품을 승인하고, 확정된 가격과 수량을 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "승인 성공: 상품 정보 업데이트 완료",
                    content = @Content(schema = @Schema(implementation = OrderItemResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청: 유효하지 않은 가격 또는 수량", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음: 관리자 권한이 없는 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "찾을 수 없음: 해당 주문 상품이 존재하지 않음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @PutMapping(value = "/{orderItemUuid}/approve", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BasicResponseDto<OrderItemResponseDto>> processFirstOrderItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID orderItemUuid,
            @RequestBody OrderItemApprovalRequestDto requestDto) {

        OrderItemResponseDto response = updateOrderItemUseCase.processFirstOderItem(
                userDetails.getUserUuid(),
                orderItemUuid,
                requestDto
        );

        return ResponseEntity.ok(BasicResponseDto.success("주문 상품 승인 및 정보 업데이트가 완료되었습니다.", response));
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


    // -- 업데이트.
    @PutMapping(value = "/{orderItemUuid}/reject")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거절 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<OrderItemResponseDto>> completedLocalDelivery(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable UUID orderItemUuid,
            @Valid @RequestBody OrderItemRejectRequestDto requestDto
    ) {

        OrderItemResponseDto orderItem = updateOrderItemUseCase.rejectOrderItem(customUserDetails.getUserUuid(), orderItemUuid, requestDto);


        return ResponseEntity.ok(BasicResponseDto.success("Successfully complete local delivery", orderItem));
    }


    // -- 조회.
    @GetMapping
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "상품 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<OrderItemResponseDto>>> getOrderItems(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(required = false) String orderItemCode,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) OrderItemStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20, page = 0) Pageable pageable
    ) {
        Page<OrderItemResponseDto> responses = getOrderItemUseCase.getSearchOrderItemsByAdmin(
                customUserDetails.getUserUuid(), orderItemCode, name, status, startDate, endDate, pageable
        );

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved orderItems", responses));
    }


    @GetMapping(value = "/{deliveryUuid}/item-detail")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 국내배송 도착 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<List<OrderItemResponseDto>>> getOrderItemByDeliveryUuid(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable UUID deliveryUuid
    ) {

        List<OrderItemResponseDto> responses = getOrderItemUseCase.getOrderItemByDeliveryUuid(customUserDetails.getUserUuid(), deliveryUuid);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved orderItem", responses));
    }

    @GetMapping(value = "/orders/{orderUuid}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 국내배송 도착 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<List<OrderItemResponseDto>>> getOrderItemByOrderUuid(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable UUID orderUuid
    ) {

        List<OrderItemResponseDto> responses = getOrderItemUseCase.getDetailsOrderItems(customUserDetails.getUserUuid(), orderUuid);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved orderItem", responses));
    }

    @Operation(summary = "주문 상품 거절 사유 조회", description = "관리자 권한으로 특정 주문 상품의 거절 사유를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공: 거절 사유 정보 반환",
                    content = @Content(schema = @Schema(implementation = OrderItemResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음: 관리자 권한이 없는 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "찾을 수 없음: 해당 주문 상품 정보를 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @GetMapping(value = "/{orderItemUuid}/reject-reason", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BasicResponseDto<OrderItemResponseDto>> getRejectReason(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID orderItemUuid) {

        OrderItemResponseDto response = getOrderItemUseCase.getRejectReason(
                userDetails.getUserUuid(),
                orderItemUuid
        );

        return ResponseEntity.ok(BasicResponseDto.success("주문 상품 거절 사유 조회가 완료되었습니다.", response));
    }

    @Operation(summary = "주문 상품 지역 주문 완료 처리", description = "관리자 권한으로 특정 주문 상품의 상태를 지역 주문 완료로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "지역 주문 완료 처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음 (관리자 권한 필요)"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 주문 상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PutMapping(value = "/{orderItemUuid}/local-completed", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BasicResponseDto<OrderItemResponseDto>> localOrderCompleted(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable(name = "orderItemUuid") UUID orderItemUuid) {

        OrderItemResponseDto response = updateOrderItemUseCase.localOrderCompleted(userDetails.getUserUuid(), orderItemUuid);

        return ResponseEntity.ok(
                BasicResponseDto.success("지역 주문 완료 처리에 성공했습니다.", response)
        );
    }

    @Operation(
            summary = "지역 배송 완료 처리",
            description = "관리자 권한을 확인한 후, 특정 주문 상품(OrderItem)의 상태를 지역 배송 완료로 변경합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "지역 배송 완료 처리 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BasicResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음 (관리자 권한 필요)", content = @Content),
            @ApiResponse(responseCode = "404", description = "사용자 또는 주문 상품을 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @PutMapping(value = "/{orderItemUuid}/local-delivery-completed", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BasicResponseDto<OrderItemResponseDto>> localDeliveryCompleted(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable(name = "orderItemUuid") UUID orderItemUuid) {

        UUID userAdminUuid = userDetails.getUserUuid();

        OrderItemResponseDto responseDto = updateOrderItemUseCase.localDeliveryCompleted(userAdminUuid, orderItemUuid);

        return ResponseEntity.ok(
                BasicResponseDto.success("지역 배송 완료 처리에 성공했습니다.", responseDto)
        );
    }

    @Operation(summary = "검수 이메일 발송 및 상태 변경", description = "관리자가 검수 사진을 첨부하여 사용자에게 이메일을 발송하고 상품 상태를 완료로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 발송 및 검수 완료 성공",
                    content = @Content(schema = @Schema(implementation = BasicResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (관리자 전용)"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 상품 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 또는 이메일 발송 실패")
    })
    @PostMapping(value = "/{orderItemUuid}/inspection", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BasicResponseDto<OrderItemResponseDto>> sendInspectionEmail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID orderItemUuid,
            @RequestPart("photos") List<MultipartFile> photos
    ) {
        OrderItemResponseDto responseDto = updateOrderItemUseCase.sendInspectionEmail(
                userDetails.getUserUuid(), orderItemUuid, photos
        );

        return ResponseEntity.ok(BasicResponseDto.success("검수 결과 이메일 발송 및 처리가 완료되었습니다.", responseDto));
    }

    @Operation(summary = "현지 상품 도착 및 통합 처리", description = "선택된 아이템들의 상태를 통합 중으로 변경하고 고객에게 알림 메일을 발송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "입고 및 통합 처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "아이템을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping(value = "/bulk-arrive", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BasicResponseDto<Void>> bulkArriveProcess(
            @RequestBody ArrivedItemRequestDto requestDto) {

        updateOrderItemUseCase.bulkArriveProcess(requestDto);

        int size = requestDto.getOrderItemUuids() != null ? requestDto.getOrderItemUuids().size() : 0;

        return ResponseEntity.ok(
                BasicResponseDto.success("총 " + size + "건의 아이템 입고 및 통합 처리가 완료되었습니다.", null)
        );
    }
}