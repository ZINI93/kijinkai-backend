package com.kijinkai.domain.payment.adapter.in.web.admin;


import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.payment.application.dto.request.DepositRequestDto;
import com.kijinkai.domain.payment.application.dto.response.DepositRequestResponseDto;
import com.kijinkai.domain.payment.application.port.in.deposit.CreateDepositUseCase;
import com.kijinkai.domain.payment.application.port.in.deposit.DeleteDepositUseCase;
import com.kijinkai.domain.payment.application.port.in.deposit.GetDepositUseCase;
import com.kijinkai.domain.payment.application.port.in.deposit.UpdateDepositUseCase;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.kijinkai.domain.payment.adapter.in.messaging.PaymentMassage.DEPOSIT_APPROVE_SUCCESS;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/admin/deposits",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class DepositAdminApiController {

    private final CreateDepositUseCase createCreatedResponse;
    private final GetDepositUseCase getDepositUseCase;
    private final UpdateDepositUseCase updateDepositUseCase;
    private final DeleteDepositUseCase deleteDepositUseCase;


    @Operation(
            summary = "입금 요청 승인",
            description = "유저가 생성한 입금 요청을 처리 합니다",
            tags = {"결제관리"}
    )
    @PostMapping("/{depositUuid}/approve")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "입금 처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "입금 내역을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<DepositRequestResponseDto>> approveDeposit(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID depositUuid,
            @RequestBody DepositRequestDto requestDto
    ) {

        DepositRequestResponseDto deposit = updateDepositUseCase.approveDepositRequest(userDetails.getUserUuid(), depositUuid, requestDto);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully completed deposit", deposit));
    }


    // 조회.


    @Operation(
            summary = "관리자가 유저의 입금 승인요청 대기 내역을 조회",
            description = "관리자가 입금 승인을 위해서 내역을 조회합니다.",
            tags = {"결제관리"}
    )
    @GetMapping("/pending")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "입금 요청 내역 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "입금 내역을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<DepositRequestResponseDto>>> getDepositRequestByPending(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PageableDefault(size = 20) Pageable pageable
    ) {

        Page<DepositRequestResponseDto> depositsByPending = getDepositUseCase.getDepositsByStatus(customUserDetails.getUserUuid(), DepositStatus.PENDING_ADMIN_APPROVAL, pageable);

        return ResponseEntity.ok(BasicResponseDto.success("Successfully retrieved deposits", depositsByPending));
    }
}



