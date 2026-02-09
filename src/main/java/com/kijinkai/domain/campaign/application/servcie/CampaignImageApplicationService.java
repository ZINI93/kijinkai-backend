package com.kijinkai.domain.campaign.application.servcie;


import com.kijinkai.domain.campaign.application.dto.request.CampaignImageCreateRequestDto;
import com.kijinkai.domain.campaign.application.port.in.campaignImage.CreateCampaignImageUseCase;
import com.kijinkai.domain.campaign.application.port.in.campaignImage.DeleteCampaignImageUseCase;
import com.kijinkai.domain.campaign.application.port.in.campaignImage.GetCampaignImageUseCase;
import com.kijinkai.domain.campaign.application.port.in.campaignImage.UpdateCampaignImageUseCase;
import com.kijinkai.domain.campaign.application.port.out.CampaignImagePersistencePort;
import com.kijinkai.domain.campaign.domain.exception.CampaignImageNotFoundException;
import com.kijinkai.domain.campaign.domain.factory.CampaignImageFactory;
import com.kijinkai.domain.campaign.domain.modal.Campaign;
import com.kijinkai.domain.campaign.domain.modal.CampaignImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CampaignImageApplicationService implements CreateCampaignImageUseCase, UpdateCampaignImageUseCase, GetCampaignImageUseCase, DeleteCampaignImageUseCase {


    private final CampaignImageFactory campaignImageFactory;
    private final CampaignImagePersistencePort campaignImagePersistencePort;

    @Override
    @Transactional
    public CampaignImage saveCampaignImage(UUID userAdminUuid, CampaignImageCreateRequestDto requestDto) {

        CampaignImage campaignImage = campaignImageFactory.createCampaignImage(userAdminUuid, requestDto);
        return campaignImagePersistencePort.saveCampaignImage(campaignImage);
    }


    @Override
    public CampaignImage getImageDetails(UUID imageUuid){

        // 조회
        return campaignImagePersistencePort.findByCampaignImageUuid(imageUuid)
                .orElseThrow(() -> new CampaignImageNotFoundException("이미지를 찾을 수 없습니다."));

    }

    @Override
    public Page<CampaignImage> getImages(Pageable pageable){

        return campaignImagePersistencePort.findAll(pageable);

    }



    @Override
    @Transactional
    public void addCampaignId(List<UUID> imageUuids, Campaign campaign){

        //조회
        List<CampaignImage> images = campaignImagePersistencePort.findAllByCampaignImageUuidIn(imageUuids);

        //추가
        images.forEach(image -> image.addCampaign(campaign));

        //저장
        campaignImagePersistencePort.saveAllCampaignImage(images);
    }


    @Override
    @Transactional
    public void updateCampaignImage(Long campaignId, List<UUID> detachImages, List<UUID> assignImages){

        // 기존 연관관계 해제
        if (!detachImages.isEmpty()) {
            campaignImagePersistencePort.detachImagesFromCampaign(campaignId, detachImages);
        }

        //기존 이미지 연관관계 추가
        if (!assignImages.isEmpty()) {
            campaignImagePersistencePort.assignImagesToCampaign(campaignId, assignImages);
        }
    }


    @Override
    @Transactional
    public void deleteImages(List<UUID> imageUuids){

        //조회
        List<CampaignImage> images = campaignImagePersistencePort.findAllByCampaignImageUuidIn(imageUuids);

        //삭제
        campaignImagePersistencePort.deleteAllCampaignImage(images);
    }




}

