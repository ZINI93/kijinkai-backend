package com.kijinkai.domain.campaign.application.dto.request;


import com.kijinkai.domain.campaign.domain.modal.CampaignImageType;
import com.kijinkai.domain.campaign.domain.modal.CampaignType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED) //
@Schema(description = "캠페인 이미지 생성 요청")
public class CampaignImageCreateRequestDto {

    private String imageUrl;
    private CampaignImageType imageType;
    private String altText;

}
