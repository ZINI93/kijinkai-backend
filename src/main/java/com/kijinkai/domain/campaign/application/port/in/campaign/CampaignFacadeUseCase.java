package com.kijinkai.domain.campaign.application.port.in.campaign;

import com.kijinkai.domain.campaign.application.dto.request.CampaignCreateRequestDto;
import com.kijinkai.domain.campaign.application.dto.request.CampaignUpdateRequestDto;
import com.kijinkai.domain.campaign.application.dto.response.CampaignResponseDto;

import java.util.UUID;

public interface CampaignFacadeUseCase {

    CampaignResponseDto createCampaign(UUID userAdminUuid, CampaignCreateRequestDto requestDto);
    CampaignResponseDto updateCampaign(UUID userAdminUuid, UUID campaignUuid, CampaignUpdateRequestDto requestDto);
    void deleteCampaign(UUID userAdminUuid, UUID campaignUuid);
}
