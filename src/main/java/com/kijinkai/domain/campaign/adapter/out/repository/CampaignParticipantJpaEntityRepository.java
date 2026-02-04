package com.kijinkai.domain.campaign.adapter.out.repository;

import com.kijinkai.domain.campaign.adapter.out.entity.CampaignParticipantJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignParticipantJpaEntityRepository extends JpaRepository<CampaignParticipantJpaEntity, Long> {
}