package com.kijinkai.domain.campaign.adapter.out.repository.campaign;

import com.kijinkai.domain.campaign.adapter.out.entity.CampaignJpaEntity;
import com.kijinkai.domain.campaign.domain.modal.CampaignStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CampaignJpaEntityRepository extends JpaRepository<CampaignJpaEntity, Long>, CampaignJpaEntityRepositoryCustom {

    Optional<CampaignJpaEntity> findByCampaignUuid(UUID campaignUuid);
    Page<CampaignJpaEntity> findAllByCampaignStatus(CampaignStatus status, Pageable pageable);

}