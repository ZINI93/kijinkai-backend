package com.kijinkai.domain.exchange.controller;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.exchange.dto.ExchangeRateRequestDto;
import com.kijinkai.domain.exchange.dto.ExchangeRateResponseDto;
import com.kijinkai.domain.exchange.dto.ExchangeRateUpdateDto;
import com.kijinkai.domain.exchange.service.ExchangeRateService;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "api/v1/exchange-rate",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ExchangeApiController {

    private final ExchangeRateService exchangeRateService;


    @Operation(
            summary = "환율정보 생성",
            description = "관리자가 환율 정보를 생성합니다",
            tags = {"환율관리"}
    )
    @PostMapping("/create")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "환율 정보 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "환율 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<ExchangeRateResponseDto>> createExchangeRate(
            Authentication authentication,
            @Valid @RequestBody ExchangeRateRequestDto requestDto
    ) {

        UUID adminUuid = getUserUuid(authentication);
        log.info("Exchange request created - admin: {}",
                adminUuid);

        try {
            ExchangeRateResponseDto response = exchangeRateService.createExchangeRate(adminUuid, requestDto);
            return createCreatedResponse("환율 생성 성공", response, "/api/v1/exchange-rate/{currency}", response.getCurrency());
        } catch (Exception e) {
            log.error("Failed to process exchangeRate request - admin: {}", adminUuid, e);
            throw e;
        }
    }

    @PostMapping("/{exchangeId}/update")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "환율 정보 업데이트 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "환율정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<ExchangeRateResponseDto>> updateExchangeRate(
            Authentication authentication,
            @PathVariable("id") Long exchangeId,
            @RequestBody ExchangeRateUpdateDto updateDto
            ) {
        UUID adminUuid = getUserUuid(authentication);
        ExchangeRateResponseDto response = exchangeRateService.updateExchangeRate(adminUuid, exchangeId, updateDto);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully updated exchange rate", response));
    }

    @GetMapping("/{exchangeId}/info")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "환율 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "환율정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<ExchangeRateResponseDto>> getExchangeRateInfo(
            @PathVariable("id") Long exchangeId
            ) {

        ExchangeRateResponseDto response = exchangeRateService.getExchangeRateInfo(exchangeId);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved exchange rate", response));
    }


    @GetMapping("/list")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "환율 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "환율정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<List<ExchangeRateResponseDto>>> getExchangeRateList(
    ) {
        List<ExchangeRateResponseDto> response = exchangeRateService.getExchangeRates();

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved exchange rate", response));
    }


    @GetMapping()

    // helper
    private static UUID getUserUuid(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUserUuid();
    }
    private <T> ResponseEntity<BasicResponseDto<T>> createCreatedResponse(
            String message,
            T data,
            String path,
            Object... pathVariables) {
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(path)
                .buildAndExpand(pathVariables)
                .toUri();
        return ResponseEntity.created(location)
                .body(BasicResponseDto.success(message, data));
    }

}
