package com.kijinkai.domain.campaign.domain.modal;

import com.kijinkai.domain.campaign.application.dto.request.CampaignUpdateRequestDto;
import lombok.*;

import java.util.UUID;


@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CampaignImage {

    private Long campaignImageId;
    private UUID campaignImageUuid;
    private UUID createdAdminUuid;

    private Campaign campaign;

    private String imageUrl;
    private CampaignImageType imageType;
    private int displayOrder;
    private String altText;


    public void addCampaign(Campaign campaign){
        this.campaign = campaign;
    }
}
