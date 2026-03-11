package com.kijinkai.domain.address.adapter.in.web;


import com.kijinkai.domain.address.application.dto.AddressResponseDto;
import com.kijinkai.domain.address.application.port.in.GetAddressUseCase;
import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.orderitem.application.dto.OrderItemResponseDto;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(
        value = "/api/v1/admin/addresses")
public class AddressAdminApiController {

    private final GetAddressUseCase getAddressUseCase;

    @GetMapping(value = "/customers/{customerUuid}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주소 조회성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "주소를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<AddressResponseDto>> getAddressByCustomer(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable UUID customerUuid
    ){

        AddressResponseDto response = getAddressUseCase.getAddressByAdmin(customUserDetails.getUserUuid(), customerUuid);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved orderItem", response));
    }


}
