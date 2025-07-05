package com.kijinkai.domain.customer.contorller;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.customer.dto.CustomerRequestDto;
import com.kijinkai.domain.customer.dto.CustomerResponseDto;
import com.kijinkai.domain.customer.dto.CustomerUpdateDto;
import com.kijinkai.domain.customer.service.CustomerService;
import com.kijinkai.domain.user.service.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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

    private final CustomerService customerService;

    @PostMapping("/register")
    @Operation(summary = "유저에서 고객으로 등록", description = "일반유저에서 고객으로 전환됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "고객으로 등록 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "고객을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<CustomerResponseDto>> createCustomer(Authentication authentication,
                                                                               @Valid @RequestBody CustomerRequestDto customerRequestDto){
        UUID userUuid = getUserUuid(authentication);
        log.info("고객 등록 요청 - 사용자 UUID: {}", userUuid);

        CustomerResponseDto customer = customerService.createCustomerWithValidate(userUuid, customerRequestDto);
        log.info("고객 등록 완료 - 고객 UUID: {}", customer.getCustomerUuid());


        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/customers/{customerUuid}")
                .buildAndExpand(customer.getCustomerUuid())
                .toUri();

        return ResponseEntity.created(location).body(BasicResponseDto.success("일반유저에서 고객으로 가입 성공",customer));
    }

    @PutMapping("/{customerUuid}")
    @Operation(summary = "고객 정보 수정", description = "유저 본인의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "고객으로 정보 수정 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "고객을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<CustomerResponseDto>> updateCustomer(@PathVariable String customerUuid,
                                                              Authentication authentication,
                                                              @Valid @RequestBody CustomerUpdateDto customerUpdateDto){
        UUID userUuid = getUserUuid(authentication);
        log.info("고객 업데이트 요청 - 사용자 UUID: {}", userUuid);

        UUID parsedCustomerUuid;

        try{
            parsedCustomerUuid = UUID.fromString(customerUuid);
        }catch (IllegalArgumentException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        CustomerResponseDto customer = customerService.updateCustomerWithValidate(
                userUuid, customerUuid, customerUpdateDto);
        log.info("고객 업데이트 완료 - 고객 UUID: {}", customer.getCustomerUuid());

        return ResponseEntity.ok(BasicResponseDto.success("고객 정보 변경 성공", customer));
    }

    @GetMapping("/me")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "고객으로 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "고객을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<CustomerResponseDto>> getCustomerInfo(Authentication authentication){
        UUID userUuid = getUserUuid(authentication);
        log.info("고객 조회 요청 - 사용자 UUID: {}", userUuid);

        CustomerResponseDto customer = customerService.getCustomerInfo(userUuid);
        log.info("고객 조회 완료 - 고객 UUID: {}", customer.getCustomerUuid());

        return ResponseEntity.ok(BasicResponseDto.success("고객 정보 조회 성공",customer));
    }

    private static UUID getUserUuid(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUserUuid();
    }
}
