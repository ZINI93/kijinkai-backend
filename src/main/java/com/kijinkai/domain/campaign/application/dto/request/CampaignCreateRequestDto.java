package com.kijinkai.domain.campaign.application.dto.request;


import com.kijinkai.domain.campaign.domain.modal.CampaignType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
@Schema(description = "캠페인 생성 요청")
public class CampaignCreateRequestDto {

    @NotEmpty(message = "최소 하나 이상의 이미지가 필요합니다.")
    @Schema(description = "캠페인 이미지 리스트 (메인, 썸네일 등)")
    private List<UUID> imageUuids;

    @Schema(description = "쿠폰 등록")
    private UUID couponUuid;

    @NotBlank(message = "캠페인 제목은 필수입니다.")
    @Size(max = 200)
    @Schema(description = "캠페인 제목")
    private String title;

    @Schema(description = "캠페인 요약 설명")
    private String description;

    @NotBlank(message = "캠페인 상세 내용은 필수입니다.")
    @Schema(description = "캠페인 상세 내용 (HTML 또는 마크다운 등)")
    private String content;

    @NotNull(message = "캠페인 유형은 필수입니다.")
    @Schema(description = "캠페인 유형 (PROMOTION, DISCOUNT 등)")
    private CampaignType campaignType;

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

    @Schema(description = "상시 캠페인 여부")
    private boolean alwaysOn;

}
