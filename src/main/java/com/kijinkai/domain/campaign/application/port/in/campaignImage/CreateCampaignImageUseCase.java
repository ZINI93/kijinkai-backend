package com.kijinkai.domain.campaign.application.port.in.campaignImage;

import com.kijinkai.domain.campaign.application.dto.request.CampaignCreateRequestDto;
import com.kijinkai.domain.campaign.application.dto.request.CampaignImageCreateRequestDto;
import com.kijinkai.domain.campaign.domain.modal.Campaign;
import com.kijinkai.domain.campaign.domain.modal.CampaignImage;
import com.kijinkai.domain.campaign.domain.modal.CampaignImageType;

import java.util.UUID;

public interface CreateCampaignImageUseCase {


    CampaignImage saveCampaignImage(UUID userAdminUuid, CampaignImageCreateRequestDto requestDto);


}
