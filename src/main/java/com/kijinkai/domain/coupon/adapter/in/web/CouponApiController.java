package com.kijinkai.domain.coupon.adapter.in.web;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.coupon.application.dto.request.CouponCreateRequestDto;
import com.kijinkai.domain.coupon.application.dto.request.CouponIssuanceRequestDto;
import com.kijinkai.domain.coupon.application.dto.response.CouponResponseDto;
import com.kijinkai.domain.coupon.application.port.in.coupon.CreateCouponUseCase;
import com.kijinkai.domain.coupon.application.port.in.coupon.DeleteCouponUseCase;
import com.kijinkai.domain.coupon.application.port.in.coupon.GetCouponUseCase;
import com.kijinkai.domain.coupon.application.port.in.coupon.UpdateCouponUseCase;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/coupons")
public class CouponApiController {


    private final CreateCouponUseCase createCouponUseCase;
    private final GetCouponUseCase getCouponUseCase;
    private final UpdateCouponUseCase updateCouponUseCase;
    private final DeleteCouponUseCase deleteCouponUseCase;



    @Operation(summary = "쿠폰 상세 조회", description = "쿠폰 코드를 통해 쿠폰의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음")
    })
    @GetMapping("/{couponCode}")
    public ResponseEntity<BasicResponseDto<CouponResponseDto>> getCouponInfo(
            @PathVariable String couponCode) {
        CouponResponseDto response = getCouponUseCase.getCouponInfo(couponCode);
        return ResponseEntity.ok(BasicResponseDto.success("쿠폰 정보 조회 성공", response));
    }



    @GetMapping(value = "/{couponCode}/info")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쿠폰 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BasicResponseDto<CouponResponseDto>> getCouponInfo(
            @PathVariable String couponCode,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        CouponResponseDto coupon = getCouponUseCase.getCouponInfo(couponCode);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved user coupon", coupon));
    }


    @Operation(summary = "쿠폰 발급", description = "사용자에게 쿠폰을 발급합니다. (동시성 제어 적용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "발급 요청 성공"),
            @ApiResponse(responseCode = "400", description = "발급 실패 또는 재고 부족"),
            @ApiResponse(responseCode = "500", description = "시스템 오류")
    })
    @PostMapping("/{couponUuid}/issue")
    public ResponseEntity<BasicResponseDto<Void>> issueCoupon(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID couponUuid) {

        updateCouponUseCase.issueCoupon(userDetails.getUserUuid(), couponUuid);
        return ResponseEntity.ok(BasicResponseDto.success("쿠폰 발급이 완료되었습니다.", null));
    }

    @Operation(summary = "쿠폰 번호로 쿠폰 발급", description = "사용자에게 쿠폰을 발급합니다. (동시성 제어 적용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "발급 요청 성공"),
            @ApiResponse(responseCode = "400", description = "발급 실패 또는 재고 부족"),
            @ApiResponse(responseCode = "500", description = "시스템 오류")
    })
    @PostMapping("/issue")
    public ResponseEntity<BasicResponseDto<Void>> issueCoupon(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CouponIssuanceRequestDto requestDto) {

        updateCouponUseCase.issueByCouponCode(userDetails.getUserUuid(), requestDto);
        return ResponseEntity.ok(BasicResponseDto.success("쿠폰 발급이 완료되었습니다.", null));
    }

}
