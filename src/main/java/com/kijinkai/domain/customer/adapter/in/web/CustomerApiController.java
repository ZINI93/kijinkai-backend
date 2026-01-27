package com.kijinkai.domain.customer.adapter.in.web;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.customer.application.dto.CustomerCreateResponse;
import com.kijinkai.domain.customer.application.dto.CustomerRequestDto;
import com.kijinkai.domain.customer.application.dto.CustomerResponseDto;
import com.kijinkai.domain.customer.application.dto.CustomerUpdateDto;
import com.kijinkai.domain.customer.application.port.in.CreateCustomerUseCase;
import com.kijinkai.domain.customer.application.port.in.GetCustomerUseCase;
import com.kijinkai.domain.customer.application.port.in.UpdateCustomerUseCase;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(
        value = "/api/v1/customers",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
@Tag(name = "customer", description = "Customer management API")
public class CustomerApiController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
//
//    @PostMapping("/register")
//    @Operation(summary = "유저에서 고객으로 등록", description = "일반유저에서 고객으로 전환됩니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "고객으로 등록 성공"),
//            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
//            @ApiResponse(responseCode = "404", description = "고객을 찾을 수 없음"),
//            @ApiResponse(responseCode = "500", description = "서버오류")
//    })
//    public ResponseEntity<BasicResponseDto<CustomerCreateResponse>> createCustomer(Authentication authentication,
//                                                                                   @Valid @RequestBody CustomerRequestDto requestDto){
//        UUID userUuid = getUserUuid(authentication);
//        log.info("고객 등록 요청 - 사용자 UUID: {}", userUuid);
//
//        CustomerCreateResponse customer = createCustomerUseCase.createCustomer(userUuid, requestDto);
//        log.info("고객 등록 완료 - 고객 UUID: {}", customer.getCustomerUuid());
//
//        URI location = ServletUriComponentsBuilder
//                .fromCurrentContextPath()
//                .path("/api/v1/customers/{customerUuid}")
//                .buildAndExpand(customer.getCustomerUuid())
//                .toUri();
//
//        return ResponseEntity.created(location).body(BasicResponseDto.success("일반유저에서 고객으로 가입 성공",customer));
//    }

//    @PostMapping("/update-info")
//    @Operation(summary = "고객 정보 수정", description = "유저 본인의 정보를 수정합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "고객으로 정보 수정 성공"),
//            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
//            @ApiResponse(responseCode = "404", description = "고객을 찾을 수 없음"),
//            @ApiResponse(responseCode = "500", description = "서버오류")
//    })
//    public ResponseEntity<BasicResponseDto<CustomerResponseDto>> updateCustomer(
//                                                              Authentication authentication,
//                                                              @Valid @RequestBody CustomerUpdateDto customerUpdateDto){
//        UUID userUuid = getUserUuid(authentication);
//        log.info("고객 업데이트 요청 - 사용자 UUID: {}", userUuid);
//
//        CustomerResponseDto customer = updateCustomerUseCase.updateCustomer(
//                userUuid, customerUpdateDto);
//        log.info("고객 업데이트 완료 - 고객 UUID: {}", customer.getCustomerUuid());
//
//        return ResponseEntity.ok(BasicResponseDto.success("고객 정보 변경 성공", customer));
//    }

    @GetMapping("/profile/me")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "고객으로 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "고객을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<CustomerResponseDto>> getCustomerInfo(Authentication authentication){
        UUID userUuid = getUserUuid(authentication);
        log.info("고객 조회 요청 - 사용자 UUID: {}", userUuid);

        CustomerResponseDto customer = getCustomerUseCase.getCustomerInfo(userUuid);
        log.info("고객 조회 완료 - 고객 UUID: {}", customer.getCustomerUuid());

        return ResponseEntity.ok(BasicResponseDto.success("고객 정보 조회 성공",customer));
    }

    private static UUID getUserUuid(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUserUuid();
    }
}
