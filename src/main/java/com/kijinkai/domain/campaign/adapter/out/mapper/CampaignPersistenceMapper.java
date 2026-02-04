package com.kijinkai.domain.campaign.adapter.out.mapper;


import com.kijinkai.domain.campaign.adapter.out.entity.CampaignJpaEntity;
import com.kijinkai.domain.campaign.domain.modal.Campaign;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CampaignPersistenceMapper {

    Campaign toCampaign(CampaignJpaEntity campaignJpaEntity);
    CampaignJpaEntity toCampaignJapEntity(Campaign campaign);


    List<Campaign> toCampaignList(List<CampaignJpaEntity> jpaEntities);
    List<CampaignJpaEntity> toCampaignJpaEntityList(List<Campaign> campaigns);

}
