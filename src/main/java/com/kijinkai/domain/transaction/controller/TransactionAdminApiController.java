package com.kijinkai.domain.transaction.controller;

import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.transaction.dto.TransactionAdminSearchResponseDto;
import com.kijinkai.domain.transaction.dto.TransactionAdminSummaryDto;
import com.kijinkai.domain.transaction.service.TransactionService;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Transaction Admin API", description = "관리자 거래 내역 관리 API")
@RestController
@RequestMapping("/api/v1/admin/transactions")
@RequiredArgsConstructor
public class TransactionAdminApiController {

    private final TransactionService transactionService;

    @Operation(summary = "관리자 거래 내역 검색 조회", description = "관리자 권한으로 필터 조건에 따른 거래 내역을 페이징 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "거래 내역 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping
    public ResponseEntity<BasicResponseDto<Page<TransactionAdminSearchResponseDto>>> getSearchTransactionByAdmin(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ParameterObject TransactionSearchConditionDto conditionDto,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<TransactionAdminSearchResponseDto> data = transactionService.getSearchTransactionByAdmin(
                userDetails.getUserUuid(),
                conditionDto,
                pageable
        );

        return ResponseEntity.ok(
                BasicResponseDto.success("거래 내역 검색 결과 조회가 완료되었습니다.", data)
        );
    }


    @Operation(summary = "거래 요약 정보 조회", description = "관리자 권한으로 당일 거래 요약 통계 데이터를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요약 정보 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "유저 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/summary")
    public ResponseEntity<BasicResponseDto<TransactionAdminSummaryDto>> getTransactionSummary(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        TransactionAdminSummaryDto data = transactionService.summary(userDetails.getUserUuid());

        return ResponseEntity.ok(
                BasicResponseDto.success("거래 요약 정보 조회가 완료되었습니다.", data)
        );
    }
}