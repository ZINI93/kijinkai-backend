package com.kijinkai.domain.campaign.application.dto.response;

import com.kijinkai.domain.campaign.domain.modal.CampaignImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "캠페인 이미지 응답")
public class CampaignImageResponseDto {

    private UUID campaignImageUuid;
    private String imageUrl;
    private CampaignImageType imageType;
    private String altText;
}
