package com.kijinkai.domain.campaign.application.port.out;

import com.kijinkai.domain.campaign.adapter.out.repository.campaign.CampaignSearchCondition;
import com.kijinkai.domain.campaign.domain.modal.Campaign;
import com.kijinkai.domain.campaign.domain.modal.CampaignStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CampaignPersistencePort {


    Campaign saveCampaign(Campaign campaign);

    Optional<Campaign> findByCampaignUuid(UUID campaignUuid);
    Page<Campaign> searchCampaign(CampaignSearchCondition condition, Pageable pageable);
    Page<Campaign> findAllByCampaignStatus(CampaignStatus status, Pageable pageable);

    void deleteCampaign(Campaign campaign);
}
