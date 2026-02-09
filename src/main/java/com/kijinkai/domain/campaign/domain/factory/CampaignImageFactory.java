package com.kijinkai.domain.campaign.domain.factory;

import com.kijinkai.domain.campaign.application.dto.request.CampaignImageCreateRequestDto;
import com.kijinkai.domain.campaign.domain.modal.CampaignImage;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CampaignImageFactory {

    public CampaignImage createCampaignImage(UUID userAdminUuid, CampaignImageCreateRequestDto requestDto){

        return CampaignImage.builder()
                .campaignImageUuid(UUID.randomUUID())
                .createdAdminUuid(userAdminUuid)
                .imageUrl(requestDto.getImageUrl())
                .imageType(requestDto.getImageType())
                .altText(requestDto.getAltText())
                .displayOrder(0)
                .build();
    }
}
