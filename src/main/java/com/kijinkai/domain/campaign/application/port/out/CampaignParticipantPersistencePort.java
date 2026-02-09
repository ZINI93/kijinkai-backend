package com.kijinkai.domain.campaign.application.port.out;

import com.kijinkai.domain.campaign.domain.modal.Campaign;
import com.kijinkai.domain.campaign.domain.modal.CampaignParticipant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CampaignParticipantPersistencePort {



    CampaignParticipant saveCampaignParticipant(CampaignParticipant campaignParticipant);

    Optional<CampaignParticipant> findByCampaignParticipantUuid(UUID campaignParticipantUuid);

    void deleteCampaignParticipant(CampaignParticipant campaignParticipant);


}



