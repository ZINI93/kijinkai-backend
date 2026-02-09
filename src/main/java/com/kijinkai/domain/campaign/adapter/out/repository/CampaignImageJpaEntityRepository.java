package com.kijinkai.domain.campaign.adapter.out.repository;

import com.kijinkai.domain.campaign.adapter.out.entity.CampaignImageJpaEntity;
import com.kijinkai.domain.campaign.domain.modal.CampaignImage;
import com.kijinkai.domain.campaign.domain.modal.CampaignImageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CampaignImageJpaEntityRepository extends JpaRepository<CampaignImageJpaEntity, Long> {

    Optional<CampaignImageJpaEntity> findByCampaignImageUuid(UUID campaignImageUuid);

    Optional<CampaignImageJpaEntity> findByCampaignCampaignUuidAndImageType(UUID campaignUuid, CampaignImageType imageType);

    List<CampaignImageJpaEntity> findAllByCampaignCampaignIdInAndImageType(List<Long> campaignIds, CampaignImageType imageType );
    List<CampaignImageJpaEntity> findAllByCampaignImageUuidIn(List<UUID> imageUuids);
    List<CampaignImage> findAllByCampaignCampaignUuid(UUID campaignUuid);

    @Modifying
    @Query("update CampaignImageJpaEntity ci set ci.campaign = null where ci.campaign.campaignId = :campaignId and ci.campaignImageUuid IN :imageUuids")
    void detachImagesFromCampaign(@Param("campaignId") Long campaignId, @Param("imageUuids") List<UUID> imageUuids);


    @Modifying
    @Query("update CampaignImageJpaEntity ci set ci.campaign.campaignId = :campaignId " +
            "where ci.campaignImageUuid IN :imageUuids")
    void assignImagesToCampaign(@Param("campaignId") Long campaignId, @Param("imageUuids") List<UUID> imageUuids);

}