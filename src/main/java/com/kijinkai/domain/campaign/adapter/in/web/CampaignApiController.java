package com.kijinkai.domain.campaign.adapter.in.web;


import com.kijinkai.domain.campaign.application.dto.response.CampaignResponseDto;
import com.kijinkai.domain.campaign.application.port.in.campaign.GetCampaignUseCase;
import com.kijinkai.domain.campaign.application.servcie.facade.CampaignFacade;
import com.kijinkai.domain.campaign.domain.modal.CampaignStatus;
import com.kijinkai.domain.common.BasicResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/campaigns", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Campaign API", description = "캠페인 관련 API (사용자)")
public class CampaignApiController {

    private final CampaignFacade campaignFacade;
    private final GetCampaignUseCase getCampaignUseCase;



    @GetMapping
    @Operation(summary = "진행중 캠페인 목록 조회", description = "진행중인 캠페인 목록을 페이징 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BasicResponseDto<Page<CampaignResponseDto>>> getCampaignsByProg(
            Pageable pageable) {

        Page<CampaignResponseDto> data = getCampaignUseCase.getCampaignsByStatus(CampaignStatus.PROGRESS,pageable);
        return ResponseEntity.ok(BasicResponseDto.success("캠페인 목록 조회가 완료되었습니다.", data));
    }


    @GetMapping("/{campaignUuid}")
    @Operation(summary = "캠페인 상세 조회", description = "특정 캠페인의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "캠페인 또는 관련 리소스 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BasicResponseDto<CampaignResponseDto>> getDetailCampaign(
            @PathVariable UUID campaignUuid) {

        CampaignResponseDto data = getCampaignUseCase.getDetailCampaign(campaignUuid);
        return ResponseEntity.ok(BasicResponseDto.success("캠페인 상세 조회가 완료되었습니다.", data));
    }
}
