package com.kijinkai.domain.campaign.adapter.out.mapper;


import com.kijinkai.domain.campaign.adapter.out.entity.CampaignParticipantJpaEntity;
import com.kijinkai.domain.campaign.domain.modal.CampaignParticipant;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CampaignParticipantPersistenceMapper {

    CampaignParticipant toCampaignParticipant(CampaignParticipantJpaEntity campaignParticipantJpaEntity);
    CampaignParticipantJpaEntity toCampaignParticipantJpaEntity(CampaignParticipant campaignParticipant);


    List<CampaignParticipant> toCampaignParticipantList(List<CampaignParticipantJpaEntity> jpaEntities);
    List<CampaignParticipantJpaEntity> toCampaignParticipantJpaEntityList(List<CampaignParticipant> campaignParticipants);

}
