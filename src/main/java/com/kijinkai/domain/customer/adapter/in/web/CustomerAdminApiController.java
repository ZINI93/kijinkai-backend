package com.kijinkai.domain.customer.adapter.in.web;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.coupon.application.dto.response.UserCouponResponseDto;
import com.kijinkai.domain.customer.application.dto.CustomerResponseDto;
import com.kijinkai.domain.customer.application.port.in.GetCustomerUseCase;
import com.kijinkai.domain.customer.domain.model.CustomerTier;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/admin/customers")
public class CustomerAdminApiController {

    private final GetCustomerUseCase getCustomerUseCase;



    @Operation(summary = "유저 리스트 조회", description = "관리자가 유저들의 리스트를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BasicResponseDto<Page<CustomerResponseDto>>> getMyCoupons(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false)CustomerTier tier,
            Pageable pageable) {

        Page<CustomerResponseDto> responses = getCustomerUseCase.getCustomers(userDetails.getUserUuid(), email, name, phoneNumber, tier, pageable);
        return ResponseEntity.ok(BasicResponseDto.success("유저 목록 조회가 완료되었습니다.", responses));
    }



}
