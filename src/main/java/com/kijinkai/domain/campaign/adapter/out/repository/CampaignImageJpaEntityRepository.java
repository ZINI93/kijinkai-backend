package com.kijinkai.domain.campaign.adapter.out.repository;

import com.kijinkai.domain.campaign.adapter.out.entity.CampaignImageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignImageJpaEntityRepository extends JpaRepository<CampaignImageJpaEntity, Long> {
}