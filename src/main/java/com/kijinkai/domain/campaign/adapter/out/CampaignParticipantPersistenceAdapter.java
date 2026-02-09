package com.kijinkai.domain.campaign.adapter.out;

import com.kijinkai.domain.campaign.adapter.out.entity.CampaignParticipantJpaEntity;
import com.kijinkai.domain.campaign.adapter.out.mapper.CampaignParticipantPersistenceMapper;
import com.kijinkai.domain.campaign.adapter.out.repository.CampaignParticipantJpaEntityRepository;
import com.kijinkai.domain.campaign.application.port.out.CampaignParticipantPersistencePort;
import com.kijinkai.domain.campaign.domain.modal.Campaign;
import com.kijinkai.domain.campaign.domain.modal.CampaignParticipant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@RequiredArgsConstructor
@Repository
public class CampaignParticipantPersistenceAdapter implements CampaignParticipantPersistencePort {

    private final CampaignParticipantJpaEntityRepository campaignParticipantJpaEntityRepository;
    private final CampaignParticipantPersistenceMapper campaignParticipantPersistenceMapper;


    @Override
    public CampaignParticipant saveCampaignParticipant(CampaignParticipant campaignParticipant) {
        CampaignParticipantJpaEntity campaignParticipantJpaEntity = campaignParticipantPersistenceMapper.toCampaignParticipantJpaEntity(campaignParticipant);
        campaignParticipantJpaEntity = campaignParticipantJpaEntityRepository.save(campaignParticipantJpaEntity);
        return campaignParticipantPersistenceMapper.toCampaignParticipant(campaignParticipantJpaEntity);
    }

    @Override
    public Optional<CampaignParticipant> findByCampaignParticipantUuid(UUID campaignParticipantUuid) {
        return campaignParticipantJpaEntityRepository.findByCampaignParticipantUuid(campaignParticipantUuid)
                .map(campaignParticipantPersistenceMapper::toCampaignParticipant);
    }

    @Override
    public void deleteCampaignParticipant(CampaignParticipant campaignParticipant) {
        CampaignParticipantJpaEntity campaignParticipantJpaEntity = campaignParticipantPersistenceMapper.toCampaignParticipantJpaEntity(campaignParticipant);
        campaignParticipantJpaEntityRepository.delete(campaignParticipantJpaEntity);
    }
}
