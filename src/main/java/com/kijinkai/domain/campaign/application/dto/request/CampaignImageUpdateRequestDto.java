package com.kijinkai.domain.campaign.application.dto.request;


import com.kijinkai.domain.campaign.domain.modal.CampaignImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter // @Value 대신 @Getter + @NoArgsConstructor 조합을 더 선호하기도 함
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED) //
@Schema(description = "캠페인 이미지 생성 요청")
public class CampaignImageUpdateRequestDto {



    private CampaignImageType imageType;
    private String altText;

}
