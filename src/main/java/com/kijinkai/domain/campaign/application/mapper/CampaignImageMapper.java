package com.kijinkai.domain.campaign.application.mapper;

import com.kijinkai.domain.campaign.application.dto.response.CampaignImageResponseDto;
import com.kijinkai.domain.campaign.domain.modal.CampaignImage;
import org.springframework.stereotype.Component;

@Component
public class CampaignImageMapper {


    public CampaignImageResponseDto toCreateResponse(CampaignImage campaignImage){

        return CampaignImageResponseDto.builder()
                .imageUrl(campaignImage.getImageUrl())
                .imageType(campaignImage.getImageType())
                .altText(campaignImage.getAltText())
                .build();

    }
    public CampaignImageResponseDto toDetailResponse(CampaignImage campaignImage){

        return CampaignImageResponseDto.builder()
                .campaignImageUuid(campaignImage.getCampaignImageUuid())
                .imageUrl(campaignImage.getImageUrl())
                .imageType(campaignImage.getImageType())
                .altText(campaignImage.getAltText())
                .build();

    }

}
