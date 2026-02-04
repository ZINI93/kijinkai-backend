package com.kijinkai.domain.campaign.adapter.out.mapper;


import com.kijinkai.domain.campaign.adapter.out.entity.CampaignImageJpaEntity;
import com.kijinkai.domain.campaign.domain.modal.CampaignImage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CampaignImagePersistenceMapper {

    CampaignImage toCampaignImage(CampaignImageJpaEntity campaignImageJpaEntity);
    CampaignImageJpaEntity toCampaignImageJapEntity(CampaignImage campaignImage);


    List<CampaignImage> toCampaignImageList(List<CampaignImageJpaEntity> jpaEntities);
    List<CampaignImageJpaEntity> toCampaignImageJpaEntityList(List<CampaignImage> campaignImages);

}
