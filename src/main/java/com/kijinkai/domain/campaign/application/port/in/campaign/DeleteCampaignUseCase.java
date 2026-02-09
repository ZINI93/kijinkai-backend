package com.kijinkai.domain.campaign.application.port.in.campaign;

import java.util.UUID;

public interface DeleteCampaignUseCase {
    void deleteCampaign(UUID campaignUuid);
}
