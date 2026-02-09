package com.kijinkai.domain.campaign.application.port.in.campaignImage;

import java.util.List;
import java.util.UUID;

public interface DeleteCampaignImageUseCase {

    void deleteImages(List<UUID> imageUuids);
}
