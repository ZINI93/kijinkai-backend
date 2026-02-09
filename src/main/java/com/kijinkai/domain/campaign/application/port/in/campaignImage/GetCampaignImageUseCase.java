package com.kijinkai.domain.campaign.application.port.in.campaignImage;

import com.kijinkai.domain.campaign.domain.modal.CampaignImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetCampaignImageUseCase {

    Page<CampaignImage> getImages(Pageable pageable);
    CampaignImage getImageDetails(UUID imageUuid);
}
