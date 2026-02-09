package com.kijinkai.domain.campaign.adapter.in.web.admin;

import com.kijinkai.domain.campaign.application.dto.request.CampaignImageCreateRequestDto;
import com.kijinkai.domain.campaign.application.dto.response.CampaignImageResponseDto;
import com.kijinkai.domain.campaign.application.dto.response.CampaignResponseDto;
import com.kijinkai.domain.campaign.application.port.in.campaignImage.CampaignImageFacadeUseCase;
import com.kijinkai.domain.campaign.application.servcie.facade.CampaignImageFacade;
import com.kijinkai.domain.campaign.domain.modal.CampaignStatus;
import com.kijinkai.domain.campaign.domain.modal.CampaignType;
import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/admin/campaign-images", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Campaign Image API", description = "캠페인 이미지 관련 관리 API")
public class CampaignImageAdminApiController {

    private final CampaignImageFacadeUseCase campaignImageFacadeUseCase;


    @Operation(summary = "캠페인 이미지 생성", description = "새로운 캠페인 이미지를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "이미지 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터", content = @Content),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @PostMapping
    public ResponseEntity<BasicResponseDto<CampaignImageResponseDto>> createCampaignImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CampaignImageCreateRequestDto requestDto) {

        CampaignImageResponseDto response = campaignImageFacadeUseCase.createCampaignImage(userDetails.getUserUuid(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BasicResponseDto.success("캠페인 이미지가 성공적으로 생성되었습니다.", response));
    }

    @Operation(summary = "캠페인 이미지 상세 조회", description = "특정 ID의 캠페인 이미지 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "이미지를 찾을 수 없음", content = @Content)
    })
    @GetMapping("/{imageUuid}")
    public ResponseEntity<BasicResponseDto<CampaignImageResponseDto>> getImageDetails(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID imageUuid) {

        CampaignImageResponseDto response = campaignImageFacadeUseCase.getImageDetails(userDetails.getUserUuid(), imageUuid);
        return ResponseEntity.ok(BasicResponseDto.success("이미지 상세 조회가 완료되었습니다.", response));
    }

    @Operation(summary = "캠페인 이미지 목록 조회", description = "페이징 처리된 캠페인 이미지 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<BasicResponseDto<Page<CampaignImageResponseDto>>> getCampaignImages(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {

        Page<CampaignImageResponseDto> response = campaignImageFacadeUseCase.getCampaignImages(userDetails.getUserUuid(), pageable);
        return ResponseEntity.ok(BasicResponseDto.success("이미지 목록 조회가 완료되었습니다.", response));
    }

    @Operation(summary = "캠페인 이미지 다중 삭제", description = "선택한 캠페인 이미지들을 일괄 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이미지 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음", content = @Content)
    })
    @DeleteMapping
    public ResponseEntity<BasicResponseDto<Void>> deleteCampaignImages(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam List<UUID> imageUuids) {

        campaignImageFacadeUseCase.deleteCampaignImages(userDetails.getUserUuid(), imageUuids);
        return ResponseEntity.ok(BasicResponseDto.success("선택한 이미지가 성공적으로 삭제되었습니다.", null));
    }
}