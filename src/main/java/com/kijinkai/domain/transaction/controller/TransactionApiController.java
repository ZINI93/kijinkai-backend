package com.kijinkai.domain.transaction.controller;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.transaction.dto.TransactionResponseDto;
import com.kijinkai.domain.transaction.entity.TransactionType;
import com.kijinkai.domain.transaction.service.TransactionService;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/transactions")
@RestController
public class TransactionApiController {


    private final TransactionService transactionService;


    @GetMapping("/account/history/top5")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거래내역 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "거래내역을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    @Operation(summary = "계좌 거래내역 최근 5건 조회", description = "입금/출금 거래내역을 최신순으로 5건 반환합니다.")
    public ResponseEntity<BasicResponseDto<List<TransactionResponseDto>>> getAccountHistoryTopFive(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        List<TransactionResponseDto> transactions = transactionService.getRecentAccountHistoryTopFive(customUserDetails.getUserUuid());

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved account history", transactions));
    }

    @GetMapping("/history")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거래내역 조회 성공"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "거래내역을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    @Operation(summary = "거래내역 전체조회", description = "거래 내역을 날짜, 구분 값으로 조회합니다.")
    public ResponseEntity<BasicResponseDto<Page<TransactionResponseDto>>> getHistory(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20, page = 0) Pageable pageable
    ) {

        Page<TransactionResponseDto> transactionHistory = transactionService.getTransactionHistory(customUserDetails.getUserUuid(), type
                , startDate, endDate, pageable);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved transaction history", transactionHistory));
    }

}
