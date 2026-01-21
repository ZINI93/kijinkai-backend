package com.kijinkai.domain.address.adapter.in.web;

import com.kijinkai.domain.address.application.dto.AddressRequestDto;
import com.kijinkai.domain.address.application.dto.AddressResponseDto;
import com.kijinkai.domain.address.application.dto.AddressUpdateDto;
import com.kijinkai.domain.address.application.port.in.CreateAddressUseCase;
import com.kijinkai.domain.address.application.port.in.DeleteAddressUseCase;
import com.kijinkai.domain.address.application.port.in.GetAddressUseCase;
import com.kijinkai.domain.address.application.port.in.UpdateAddressUseCase;
import com.kijinkai.domain.common.BasicResponseDto;
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
@RestController
@RequiredArgsConstructor
@RequestMapping(
        value = "/api/v1/addresses")
public class AddressApiController {

    private final CreateAddressUseCase createAddressUseCase;
    private final GetAddressUseCase getAddressUseCase;
    private final UpdateAddressUseCase updateAddressUseCase;
    private final DeleteAddressUseCase deleteAddressUseCase;


    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "주소 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "주소를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<AddressResponseDto>> createAddress(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody AddressRequestDto requestDto
    ) {

        log.info("User: {} requests address create", customUserDetails.getUserUuid());

        AddressResponseDto address = createAddressUseCase.createAddress(customUserDetails.getUserUuid(), requestDto);
        log.info("AddressJpaEntity:{} successfully created by user{}", address.getAddressUuid(), customUserDetails.getUserUuid());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{addressUuid}")
                .buildAndExpand(address.getAddressUuid())
                .toUri();

        return ResponseEntity.created(location).body(BasicResponseDto.success("Successful created address", address));
    }


    @PutMapping("/{addressUuid}/update")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주소 업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "주소를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<AddressResponseDto>> updateAddress(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable UUID addressUuid,
            @Valid @RequestBody AddressUpdateDto updateDto
    ) {

        AddressResponseDto address = updateAddressUseCase.updateAddress(customUserDetails.getUserUuid(), addressUuid, updateDto);
        log.info("AddressJpaEntity: {}, successfully updated by user {}", address.getAddressUuid(), customUserDetails.getUserUuid());


        return ResponseEntity.ok(BasicResponseDto.success("Successful update address", address));
    }

    @GetMapping("/info")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주소정보 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "주소를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<AddressResponseDto>> getAddressInfo(
            Authentication authentication
    ) {
        UUID userUuid = getUserUuid(authentication);
        log.info("User: {} requests address retrieved", userUuid);

        AddressResponseDto address = getAddressUseCase.getAddressInfo(userUuid);
        log.info("AddressJpaEntity: {} successfully retrieved by user {}", address.getAddressUuid(), userUuid);


        return ResponseEntity.ok(BasicResponseDto.success("Successful retrieved address info", address));
    }

    @DeleteMapping("/{addressUuid}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주소정보 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "주소를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<Void> deleteAddress(
            Authentication authentication,
            @PathVariable UUID addressUuid

    ) {
        UUID userUuid = getUserUuid(authentication);
        log.info("User: {} requests address delete", userUuid);

        deleteAddressUseCase.deleteAddress(userUuid, addressUuid);

        return ResponseEntity.noContent().build();
    }


    private static UUID getUserUuid(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUserUuid();
    }

}

