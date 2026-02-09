package com.kijinkai.domain.campaign.adapter.out.repository;

import com.kijinkai.domain.campaign.adapter.out.entity.CampaignParticipantJpaEntity;
import com.kijinkai.domain.campaign.domain.modal.CampaignParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CampaignParticipantJpaEntityRepository extends JpaRepository<CampaignParticipantJpaEntity, Long> {

    Optional<CampaignParticipantJpaEntity> findByCampaignParticipantUuid(UUID campaignParticipantUuid);
}