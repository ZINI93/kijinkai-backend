package com.kijinkai.domain.campaign.adapter.out.repository;

import com.kijinkai.domain.campaign.adapter.out.entity.CampaignJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignJpaEntityRepository extends JpaRepository<CampaignJpaEntity, Long> {
}