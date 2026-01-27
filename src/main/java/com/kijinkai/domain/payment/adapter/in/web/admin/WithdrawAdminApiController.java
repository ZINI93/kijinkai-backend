package com.kijinkai.domain.payment.adapter.in.web.admin;

import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.payment.application.dto.request.WithdrawRequestDto;
import com.kijinkai.domain.payment.application.dto.response.WithdrawResponseDto;
import com.kijinkai.domain.payment.application.port.in.withdraw.CreateWithdrawUseCase;
import com.kijinkai.domain.payment.application.port.in.withdraw.DeleteWithdrawUseCase;
import com.kijinkai.domain.payment.application.port.in.withdraw.GetWithdrawUseCase;
import com.kijinkai.domain.payment.application.port.in.withdraw.UpdateWithdrawUseCase;
import com.kijinkai.domain.payment.domain.enums.WithdrawStatus;
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

import static com.kijinkai.domain.payment.adapter.in.messaging.PaymentMassage.WITHDRAW_APPROVE_SUCCESS;
import static com.kijinkai.domain.payment.adapter.in.messaging.PaymentMassage.WITHDRAW_RETRIEVED_SUCCESS;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/admin/withdraws",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class WithdrawAdminApiController {

    private final CreateWithdrawUseCase createWithdrawUseCase;
    private final GetWithdrawUseCase getWithdrawUseCase;
    private final UpdateWithdrawUseCase updateWithdrawUseCase;
    private final DeleteWithdrawUseCase deleteWithdrawUseCase;


    @Operation(
            summary = "관리자가 출금 요청  승인",
            description = "관리자가 유저가 요청한 출금 요청을 처리 합니다.",
            tags = {"결제관리"}
    )
    @PutMapping("/{requestUuid}/approve")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "출근 승인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "출금내역을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<WithdrawResponseDto>> approveWithdraw(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable UUID requestUuid,
            @RequestBody WithdrawRequestDto requestDto
    ) {
        log.info("Withdraw request approved - admin: {}", customUserDetails.getUserUuid());

        try {
            WithdrawResponseDto response = updateWithdrawUseCase.approveWithdrawRequest(customUserDetails.getUserUuid(), requestUuid, requestDto);
            return ResponseEntity.ok(BasicResponseDto.success(WITHDRAW_APPROVE_SUCCESS, response));
        } catch (Exception e) {
            log.error("Failed to process withdraw request - admin: {}", customUserDetails.getUserUuid(), e);
            throw e;
        }
    }


    @Operation(
            summary = "관리자가 유저의 출금 승인요청 대기 내역을 조회",
            description = "관리자가 출금 승인을 위해서 내역을 조회합니다.",
            tags = {"결제관리"}
    )
    @GetMapping("/pending")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "입금 요청 내역 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "입금 내역을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버오류")
    })
    public ResponseEntity<BasicResponseDto<Page<WithdrawResponseDto>>> getWithdrawsByPending(
            @PageableDefault(size = 20, page = 0) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        try {
            Page<WithdrawResponseDto> withdraws = getWithdrawUseCase.getWithdrawsByStatus(customUserDetails.getUserUuid(), WithdrawStatus.PENDING_ADMIN_APPROVAL, pageable);

            return ResponseEntity.ok(BasicResponseDto.success(WITHDRAW_RETRIEVED_SUCCESS, withdraws));
        } catch (Exception e) {
            log.error("Failed to process deposit request - admin: {}", customUserDetails.getUserUuid(), e);
            throw e;
        }
    }


}
