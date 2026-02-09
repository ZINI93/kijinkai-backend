package com.kijinkai.domain.campaign.application.port.in.campaign;

import com.kijinkai.domain.campaign.application.dto.request.CampaignCreateRequestDto;
import com.kijinkai.domain.campaign.application.dto.response.CampaignResponseDto;
import com.kijinkai.domain.campaign.domain.modal.Campaign;

import java.util.UUID;

public interface CreateCampaignUseCase {

    CampaignResponseDto saveCampaign(UUID userAdminUuid, CampaignCreateRequestDto requestDto);
}
