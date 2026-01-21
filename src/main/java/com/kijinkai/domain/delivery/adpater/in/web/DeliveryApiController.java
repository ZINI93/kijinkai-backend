package com.kijinkai.domain.delivery.adpater.in.web;


import com.kijinkai.domain.common.BaseController;
import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.delivery.application.dto.DeliveryRequestDto;
import com.kijinkai.domain.delivery.application.dto.DeliveryResponseDto;
import com.kijinkai.domain.delivery.application.in.CreateDeliveryUseCase;
import com.kijinkai.domain.delivery.application.in.DeleteDeliveryUseCase;
import com.kijinkai.domain.delivery.application.in.GetDeliveryUseCase;
import com.kijinkai.domain.delivery.application.in.UpdateDeliveryUseCase;
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
public class DeliveryApiController extends BaseController {

    private final CreateDeliveryUseCase createDeliveryUseCase;
    private final GetDeliveryUseCase getDeliveryUseCase;
    private final UpdateDeliveryUseCase updateDeliveryUseCase;
    private final DeleteDeliveryUseCase deleteDelivery;

    @PostMapping(value = "/{addressUuid}/request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송 요청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력값 (Validation 에러)"), // 404 대신 400 권장
            @ApiResponse(responseCode = "404", description = "배송을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BasicResponseDto<UUID>> requestDelivery(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable UUID addressUuid,
            @Valid @RequestBody DeliveryRequestDto requestDto
    ) {
        UUID deliveryUuid = createDeliveryUseCase.requestDelivery(customUserDetails.getUserUuid(), addressUuid, requestDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/deliveries/{deliveryUuid}")
                .buildAndExpand(deliveryUuid)
                .toUri();

        return ResponseEntity.created(location).body(BasicResponseDto.success("Successfully created delivery", deliveryUuid));
    }

    @GetMapping("/{deliveryUuid}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송정보 요청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "배송을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<DeliveryResponseDto>> getDeliveryInfo(
            Authentication authentication,
            @PathVariable UUID deliveryUuid
    ) {

        UUID userUuid = getUserUuid(authentication);
        DeliveryResponseDto delivery = getDeliveryUseCase.getDeliveryInfo(userUuid, deliveryUuid);

        return ResponseEntity.ok(BasicResponseDto.success("Successful retrieved delivery", delivery));
    }
}
