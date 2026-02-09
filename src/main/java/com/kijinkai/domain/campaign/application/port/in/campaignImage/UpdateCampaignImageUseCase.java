package com.kijinkai.domain.campaign.application.port.in.campaignImage;


import com.kijinkai.domain.campaign.domain.modal.Campaign;

import java.util.List;
import java.util.UUID;

public interface UpdateCampaignImageUseCase {

    void addCampaignId(List<UUID> imageUuids, Campaign campaign);

    void updateCampaignImage(Long campaignId, List<UUID> detachImages, List<UUID> assignImages);
}
