package com.kijinkai.domain.campaign.adapter.out;

import com.kijinkai.domain.campaign.adapter.out.entity.CampaignJpaEntity;
import com.kijinkai.domain.campaign.adapter.out.mapper.CampaignPersistenceMapper;
import com.kijinkai.domain.campaign.adapter.out.repository.campaign.CampaignJpaEntityRepository;
import com.kijinkai.domain.campaign.adapter.out.repository.campaign.CampaignSearchCondition;
import com.kijinkai.domain.campaign.application.port.out.CampaignPersistencePort;
import com.kijinkai.domain.campaign.domain.modal.Campaign;
import com.kijinkai.domain.campaign.domain.modal.CampaignStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class CampaignPersistenceAdapter implements CampaignPersistencePort {


    private final CampaignJpaEntityRepository campaignJpaEntityRepository;
    private final CampaignPersistenceMapper campaignPersistenceMapper;

    @Override
    public Campaign saveCampaign(Campaign campaign) {
        CampaignJpaEntity campaignJapEntity = campaignPersistenceMapper.toCampaignJapEntity(campaign);
        campaignJapEntity = campaignJpaEntityRepository.save(campaignJapEntity);
        return campaignPersistenceMapper.toCampaign(campaignJapEntity);
    }

    @Override
    public Optional<Campaign> findByCampaignUuid(UUID campaignUuid) {
        return campaignJpaEntityRepository.findByCampaignUuid(campaignUuid)
                .map(campaignPersistenceMapper::toCampaign);
    }

    @Override
    public Page<Campaign> searchCampaign(CampaignSearchCondition condition, Pageable pageable) {
        return campaignJpaEntityRepository.searchCampaign(condition, pageable)
                .map(campaignPersistenceMapper::toCampaign);
    }

    @Override
    public Page<Campaign> findAllByCampaignStatus(CampaignStatus status, Pageable pageable) {
        return campaignJpaEntityRepository.findAllByCampaignStatus(status,pageable)
                .map(campaignPersistenceMapper::toCampaign);
    }

    @Override
    public void deleteCampaign(Campaign campaign) {
        CampaignJpaEntity campaignJapEntity = campaignPersistenceMapper.toCampaignJapEntity(campaign);
        campaignJpaEntityRepository.delete(campaignJapEntity);
    }
}
