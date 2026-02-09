package com.kijinkai.domain.campaign.application.port.out;

import com.kijinkai.domain.campaign.domain.modal.CampaignImage;
import com.kijinkai.domain.campaign.domain.modal.CampaignImageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CampaignImagePersistencePort {


    CampaignImage saveCampaignImage(CampaignImage campaignImage);
    List<CampaignImage> saveAllCampaignImage(List<CampaignImage> campaignImages);

    Optional<CampaignImage> findByCampaignImageUuid(UUID campaignImageUuid);
    Optional<CampaignImage> findByCampaignCampaignUuidAndImageType(UUID campaignUuid, CampaignImageType imageType);

    List<CampaignImage> findAllByCampaignCampaignIdInAndImageType(List<Long> campaignId, CampaignImageType imageType);
    List<CampaignImage> findAllByCampaignImageUuidIn(List<UUID> imageUuids);
    Page<CampaignImage> findAll(Pageable pageable);

    void assignImagesToCampaign(Long campaignId, List<UUID> imageUuids);
    void detachImagesFromCampaign(Long campaignId, List<UUID> detachImages);
    void deleteCampaign(CampaignImage campaignImage);
    void deleteAllCampaignImage(List<CampaignImage> campaignImages);
}

