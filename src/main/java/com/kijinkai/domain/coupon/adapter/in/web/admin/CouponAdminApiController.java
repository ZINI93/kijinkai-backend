package com.kijinkai.domain.coupon.adapter.in.web.admin;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.coupon.application.dto.request.CouponCreateRequestDto;
import com.kijinkai.domain.coupon.application.dto.request.CouponUpdateRequestDto;
import com.kijinkai.domain.coupon.application.dto.response.CouponResponseDto;
import com.kijinkai.domain.coupon.application.port.in.coupon.CreateCouponUseCase;
import com.kijinkai.domain.coupon.application.port.in.coupon.DeleteCouponUseCase;
import com.kijinkai.domain.coupon.application.port.in.coupon.GetCouponUseCase;
import com.kijinkai.domain.coupon.application.port.in.coupon.UpdateCouponUseCase;
import com.kijinkai.domain.coupon.domain.modal.DiscountType;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/admin/coupons")
public class CouponAdminApiController {


    private final CreateCouponUseCase createCouponUseCase;
    private final GetCouponUseCase getCouponUseCase;
    private final UpdateCouponUseCase updateCouponUseCase;
    private final DeleteCouponUseCase deleteCouponUseCase;

    @Operation(summary = "쿠폰 생성", description = "관리자 권한으로 새로운 쿠폰을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "쿠폰 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping
    public ResponseEntity<BasicResponseDto<CouponResponseDto>> createCoupon(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CouponCreateRequestDto requestDto) {

        CouponResponseDto data = createCouponUseCase.createCoupon(userDetails.getUserUuid(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BasicResponseDto.success("쿠폰이 성공적으로 생성되었습니다.", data));
    }


    @Operation(summary = "쿠폰 목록 조회(조건별)", description = "다양한 검색 조건과 페이징을 사용하여 쿠폰 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<BasicResponseDto<Page<CouponResponseDto>>> getCoupons(
            @RequestParam(required = false) UUID campaignUuid,
            @RequestParam(required = false) String couponCode,
            @RequestParam(required = false) DiscountType type,
            @RequestParam(required = false) Integer minTotalQuantity,
            @RequestParam(required = false) Integer maxTotalQuantity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validUntil,
            @RequestParam(required = false) Boolean isActive,
            Pageable pageable) {
        Page<CouponResponseDto> response = getCouponUseCase.getCoupons(
                campaignUuid, couponCode, type, minTotalQuantity, maxTotalQuantity,
                validFrom, validUntil, isActive, pageable);
        return ResponseEntity.ok(BasicResponseDto.success("쿠폰 목록 조회 성공", response));
    }


    @Operation(summary = "쿠폰 수정", description = "비활성화 상태의 쿠폰 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "수정 불가 상태"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음")
    })
    @PutMapping("/{couponUuid}")
    public ResponseEntity<BasicResponseDto<CouponResponseDto>> updateCoupon(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID couponUuid,
            @RequestBody CouponUpdateRequestDto requestDto) {

        CouponResponseDto data = updateCouponUseCase.updateCoupon(userDetails.getUserUuid(), couponUuid, requestDto);
        return ResponseEntity.ok(BasicResponseDto.success("쿠폰 수정이 완료되었습니다.", data));
    }


    @Operation(summary = "쿠폰 활성화", description = "관리자가 특정 쿠폰을 활성화합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "활성화 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음")
    })
    @PutMapping("/{couponUuid}/active")
    public ResponseEntity<BasicResponseDto<String>> activeCoupon(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID couponUuid) {

        String couponCode = updateCouponUseCase.activeCoupon(userDetails.getUserUuid(), couponUuid);
        return ResponseEntity.ok(BasicResponseDto.success("쿠폰이 활성화되었습니다.", couponCode));
    }

    @Operation(summary = "쿠폰 삭제", description = "비활성화된 쿠폰을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "활성화 중인 쿠폰 삭제 불가"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음")
    })
    @DeleteMapping("/{couponUuid}")
    public ResponseEntity<BasicResponseDto<Void>> deleteCoupon(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID couponUuid) {

        deleteCouponUseCase.deleteCoupon(userDetails.getUserUuid(), couponUuid);
        return ResponseEntity.ok(BasicResponseDto.success("쿠폰이 삭제되었습니다.", null));
    }

}
