package com.kijinkai.domain.campaign.adapter.in.web.admin;


import com.kijinkai.domain.campaign.application.dto.request.CampaignCreateRequestDto;
import com.kijinkai.domain.campaign.application.dto.request.CampaignUpdateRequestDto;
import com.kijinkai.domain.campaign.application.dto.response.CampaignResponseDto;
import com.kijinkai.domain.campaign.application.port.in.campaign.GetCampaignUseCase;
import com.kijinkai.domain.campaign.application.servcie.facade.CampaignFacade;
import com.kijinkai.domain.campaign.domain.modal.CampaignStatus;
import com.kijinkai.domain.campaign.domain.modal.CampaignType;
import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/admin/campaigns", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Campaign API", description = "캠페인 관련 API (사용자 및 관리자)")
public class CampaignAdminApiController {

    private final CampaignFacade campaignFacade;
    private final GetCampaignUseCase getCampaignUseCase;


    @PostMapping
    @Operation(summary = "캠페인 생성 (관리자)", description = "관리자 권한으로 새로운 캠페인을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BasicResponseDto<CampaignResponseDto>> createCampaign(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CampaignCreateRequestDto requestDto) {

        CampaignResponseDto data = campaignFacade.createCampaign(userDetails.getUserUuid(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BasicResponseDto.success("캠페인이 성공적으로 생성되었습니다.", data));
    }

    @PutMapping("/{campaignUuid}")
    @Operation(summary = "캠페인 수정 (관리자)", description = "관리자 권한으로 기존 캠페인 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "캠페인 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BasicResponseDto<CampaignResponseDto>> updateCampaign(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID campaignUuid,
            @RequestBody CampaignUpdateRequestDto requestDto) {

        CampaignResponseDto data = campaignFacade.updateCampaign(userDetails.getUserUuid(), campaignUuid, requestDto);
        return ResponseEntity.ok(BasicResponseDto.success("캠페인이 성공적으로 수정되었습니다.", data));
    }

    @DeleteMapping("/{campaignUuid}")
    @Operation(summary = "캠페인 삭제 (관리자)", description = "관리자 권한으로 캠페인을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "캠페인 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BasicResponseDto<Void>> deleteCampaign(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID campaignUuid) {

        campaignFacade.deleteCampaign(userDetails.getUserUuid(), campaignUuid);
        return ResponseEntity.ok(BasicResponseDto.success("캠페인이 성공적으로 삭제되었습니다.", null));
    }


    @GetMapping
    @Operation(summary = "캠페인 목록 조회", description = "조건에 맞는 캠페인 목록을 페이징 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BasicResponseDto<Page<CampaignResponseDto>>> getCampaigns(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) CampaignType type,
            @RequestParam(required = false) CampaignStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) Integer minParticipants,
            Pageable pageable) {

        Page<CampaignResponseDto> data = getCampaignUseCase.getCampaigns(title, type, status, startDate, endDate, featured, minParticipants, pageable);
        return ResponseEntity.ok(BasicResponseDto.success("캠페인 목록 조회가 완료되었습니다.", data));
    }


}
