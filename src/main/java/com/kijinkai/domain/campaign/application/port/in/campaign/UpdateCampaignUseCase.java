package com.kijinkai.domain.campaign.application.port.in.campaign;

import com.kijinkai.domain.campaign.application.dto.request.CampaignUpdateRequestDto;
import com.kijinkai.domain.campaign.application.dto.response.CampaignResponseDto;
import com.kijinkai.domain.campaign.domain.modal.Campaign;
import com.kijinkai.domain.campaign.domain.modal.CampaignImage;

public interface UpdateCampaignUseCase {
    CampaignResponseDto updateCampaign(Campaign campaign, CampaignUpdateRequestDto requestDto);

}
