package com.kijinkai.domain.campaign.adapter.out;

import com.kijinkai.domain.campaign.adapter.out.entity.CampaignImageJpaEntity;
import com.kijinkai.domain.campaign.adapter.out.mapper.CampaignImagePersistenceMapper;
import com.kijinkai.domain.campaign.adapter.out.repository.CampaignImageJpaEntityRepository;
import com.kijinkai.domain.campaign.application.port.out.CampaignImagePersistencePort;
import com.kijinkai.domain.campaign.domain.modal.CampaignImage;
import com.kijinkai.domain.campaign.domain.modal.CampaignImageType;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

@RequiredArgsConstructor
@Repository
public class CampaignImagePersistenceAdapter implements CampaignImagePersistencePort {

    private final CampaignImageJpaEntityRepository campaignImageJpaEntityRepository;
    private final CampaignImagePersistenceMapper campaignImagePersistenceMapper;


    @Override
    public CampaignImage saveCampaignImage(CampaignImage campaignImage) {
        CampaignImageJpaEntity campaignImageJapEntity = campaignImagePersistenceMapper.toCampaignImageJpaEntity(campaignImage);
        campaignImageJapEntity = campaignImageJpaEntityRepository.save(campaignImageJapEntity);
        return campaignImagePersistenceMapper.toCampaignImage(campaignImageJapEntity);

    }

    @Override
    public List<CampaignImage> saveAllCampaignImage(List<CampaignImage> campaignImages) {
        List<CampaignImageJpaEntity> campaignImageJpaEntityList = campaignImagePersistenceMapper.toCampaignImageJpaEntityList(campaignImages);
        campaignImageJpaEntityList = campaignImageJpaEntityRepository.saveAll(campaignImageJpaEntityList);
        return campaignImagePersistenceMapper.toCampaignImageList(campaignImageJpaEntityList);

    }

    @Override
    public void deleteAllCampaignImage(List<CampaignImage> campaignImages) {
        List<CampaignImageJpaEntity> campaignImageJpaEntityList = campaignImagePersistenceMapper.toCampaignImageJpaEntityList(campaignImages);
        campaignImageJpaEntityRepository.deleteAll(campaignImageJpaEntityList);
    }

    @Override
    public Optional<CampaignImage> findByCampaignImageUuid(UUID campaignImageUuid) {
        return campaignImageJpaEntityRepository.findByCampaignImageUuid(campaignImageUuid)
                .map(campaignImagePersistenceMapper::toCampaignImage);
    }

    @Override
    public Optional<CampaignImage> findByCampaignCampaignUuidAndImageType(UUID campaignUuid, CampaignImageType imageType) {
        return campaignImageJpaEntityRepository.findByCampaignCampaignUuidAndImageType(campaignUuid, imageType)
                .map(campaignImagePersistenceMapper::toCampaignImage);
    }

    @Override
    public List<CampaignImage> findAllByCampaignCampaignIdInAndImageType(List<Long> campaignIds, CampaignImageType imageType) {
        List<CampaignImageJpaEntity> jpaImages = campaignImageJpaEntityRepository.findAllByCampaignCampaignIdInAndImageType(campaignIds, imageType);
        return campaignImagePersistenceMapper.toCampaignImageList(jpaImages);
    }

    @Override
    public List<CampaignImage> findAllByCampaignImageUuidIn(List<UUID> imageUuids) {
        List<CampaignImageJpaEntity> jpaImages = campaignImageJpaEntityRepository.findAllByCampaignImageUuidIn(imageUuids);
        return campaignImagePersistenceMapper.toCampaignImageList(jpaImages);
    }

    @Override
    public Page<CampaignImage> findAll(Pageable pageable) {
        return campaignImageJpaEntityRepository.findAll(pageable)
                .map(campaignImagePersistenceMapper::toCampaignImage);
    }

    @Override
    public void assignImagesToCampaign(Long campaignId, List<UUID> imageUuids) {
        campaignImageJpaEntityRepository.assignImagesToCampaign(campaignId, imageUuids);

    }

    @Override
    public void detachImagesFromCampaign(Long campaignId, List<UUID> imageUuids) {
        campaignImageJpaEntityRepository.detachImagesFromCampaign(campaignId, imageUuids);
    }

    @Override
    public void deleteCampaign(CampaignImage campaignImage) {
        CampaignImageJpaEntity campaignImageJapEntity = campaignImagePersistenceMapper.toCampaignImageJpaEntity(campaignImage);
        campaignImageJpaEntityRepository.delete(campaignImageJapEntity);
    }
}
