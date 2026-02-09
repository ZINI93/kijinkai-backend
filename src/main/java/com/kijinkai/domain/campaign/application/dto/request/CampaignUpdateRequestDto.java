package com.kijinkai.domain.campaign.application.dto.request;


import com.kijinkai.domain.campaign.domain.modal.CampaignType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter // @Value 대신 @Getter + @NoArgsConstructor 조합을 더 선호하기도 함
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED) //
@Schema(description = "캠페인 변경 요청")
public class CampaignUpdateRequestDto {




    private List<UUID> detachImageUuids;
    private List<UUID> assignImageUuids;



    @NotBlank(message = "메인 이미지는 필수입니다.")
    @Schema(description = "메인 이미지 URL", example = "https://cdn.example.com/main.jpg")
    private String mainImageUrl;

    @Schema(description = "메인 이미지 UUID (기존 이미지 유지 시 필요)")
    private UUID mainImageUuid;

    @NotBlank(message = "썸네일 이미지는 필수입니다.")
    @Schema(description = "썸네일 이미지 URL", example = "https://cdn.example.com/thumb.jpg")
    private String thumbnailImageUrl;

    @Schema(description = "썸네일 이미지 UUID (기존 이미지 유지 시 필요)")
    private UUID thumbnailImageUuid;

    // --- 기본 정보 ---

    @NotBlank(message = "캠페인 제목은 필수입니다.")
    @Size(max = 200)
    @Schema(description = "캠페인 제목", example = "2024 신년 감사 이벤트")
    private String title;

    @Schema(description = "캠페인 요약 설명", example = "새해를 맞아 전 품목 할인 이벤트를 진행합니다.")
    private String description;

    @NotBlank(message = "캠페인 상세 내용은 필수입니다.")
    @Schema(description = "캠페인 상세 내용 (HTML 또는 마크다운)", example = "<p>이벤트 내용...</p>")
    private String content;

    @NotNull(message = "캠페인 유형은 필수입니다.")
    @Schema(description = "캠페인 유형 (PROMOTION, DISCOUNT, EVENT 등)")
    private CampaignType campaignType;

    // --- 노출 및 일정 정보 ---

    @NotNull(message = "시작 일시는 필수입니다.")
    @Schema(description = "캠페인 시작 일시")
    private LocalDateTime startDate;

    @NotNull(message = "종료 일시는 필수입니다.")
    @Schema(description = "캠페인 종료 일시")
    private LocalDateTime endDate;

    @Schema(description = "메인 노출 여부", defaultValue = "false")
    private boolean featured;

    @Schema(description = "노출 순서", defaultValue = "0")
    private int displayOrder;

}
