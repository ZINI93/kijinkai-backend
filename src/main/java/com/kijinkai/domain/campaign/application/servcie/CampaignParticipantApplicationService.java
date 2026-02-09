package com.kijinkai.domain.campaign.application.servcie;

import com.kijinkai.domain.campaign.application.port.in.campaignParticipant.CreateCampaignParticipantUseCase;
import com.kijinkai.domain.campaign.application.port.in.campaignParticipant.DeleteCampaignParticipantUseCase;
import com.kijinkai.domain.campaign.application.port.in.campaignParticipant.GetCampaignParticipantUseCase;
import com.kijinkai.domain.campaign.application.port.in.campaignParticipant.UpdateCampaignParticipantUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CampaignParticipantApplicationService implements CreateCampaignParticipantUseCase, GetCampaignParticipantUseCase, UpdateCampaignParticipantUseCase, DeleteCampaignParticipantUseCase {
}
