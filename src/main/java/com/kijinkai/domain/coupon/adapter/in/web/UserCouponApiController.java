package com.kijinkai.domain.coupon.adapter.in.web;

import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.coupon.application.dto.response.UserCouponResponseDto;
import com.kijinkai.domain.coupon.application.port.in.usercoupon.CreateUserCouponUseCase;
import com.kijinkai.domain.coupon.application.port.in.usercoupon.DeleteUserCouponUseCase;
import com.kijinkai.domain.coupon.application.port.in.usercoupon.GetUserCouponUseCase;
import com.kijinkai.domain.coupon.application.port.in.usercoupon.UpdateUserCouponUseCase;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-coupons")
@Tag(name = "User Coupon API", description = "유저 쿠폰 관리 API")
public class UserCouponApiController {


    private final CreateUserCouponUseCase createUserCouponUseCase;
    private final GetUserCouponUseCase getUserCouponUseCase;
    private final UpdateUserCouponUseCase updateUserCouponUseCase;
    private final DeleteUserCouponUseCase deleteUserCouponUseCase;



    @Operation(summary = "내 쿠폰 목록 조회", description = "로그인한 유저가 보유한 모든 쿠폰을 페이징 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BasicResponseDto<Page<UserCouponResponseDto>>> getMyCoupons(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {

        Page<UserCouponResponseDto> responses = getUserCouponUseCase.getMyCoupons(userDetails.getUserUuid(), pageable);
        return ResponseEntity.ok(BasicResponseDto.success("쿠폰 목록 조회가 완료되었습니다.", responses));
    }


    @Operation(summary = "내 쿠폰 상세 조회", description = "특정 유저 쿠폰의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 쿠폰", content = @Content)
    })
    @GetMapping(value = "/{userCouponUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BasicResponseDto<UserCouponResponseDto>> getMyCouponInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID userCouponUuid) {

        UserCouponResponseDto response = getUserCouponUseCase.getMyCouponInfo(userDetails.getUserUuid(), userCouponUuid);
        return ResponseEntity.ok(BasicResponseDto.success("쿠폰 상세 조회가 완료되었습니다.", response));
    }


    @Operation(summary = "쿠폰 삭제", description = "보유 중인 유저 쿠폰을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "쿠폰 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "삭제할 쿠폰을 찾을 수 없음", content = @Content)
    })
    @DeleteMapping(value = "/{userCouponUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BasicResponseDto<Void>> deleteCoupon(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID userCouponUuid) {

        deleteUserCouponUseCase.deleteCoupon(userDetails.getUserUuid(), userCouponUuid);
        return ResponseEntity.ok(BasicResponseDto.success("쿠폰이 성공적으로 삭제되었습니다.", null));
    }

}
