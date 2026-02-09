package com.kijinkai.domain.campaign.adapter.out.repository.campaign;

import com.kijinkai.domain.campaign.adapter.out.entity.CampaignJpaEntity;
import com.kijinkai.domain.coupon.adapter.out.entity.CouponJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CampaignJpaEntityRepositoryCustom {

    Page<CampaignJpaEntity> searchCampaign(CampaignSearchCondition condition, Pageable pageable);
}