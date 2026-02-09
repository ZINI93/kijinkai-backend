package com.kijinkai.domain.campaign.application.servcie;

import com.kijinkai.domain.campaign.adapter.out.repository.campaign.CampaignSearchCondition;
import com.kijinkai.domain.campaign.application.dto.request.CampaignCreateRequestDto;
import com.kijinkai.domain.campaign.application.dto.request.CampaignUpdateRequestDto;
import com.kijinkai.domain.campaign.application.dto.response.CampaignResponseDto;
import com.kijinkai.domain.campaign.application.mapper.CampaignMapper;
import com.kijinkai.domain.campaign.application.port.in.campaign.CreateCampaignUseCase;
import com.kijinkai.domain.campaign.application.port.in.campaign.DeleteCampaignUseCase;
import com.kijinkai.domain.campaign.application.port.in.campaign.GetCampaignUseCase;
import com.kijinkai.domain.campaign.application.port.in.campaign.UpdateCampaignUseCase;
import com.kijinkai.domain.campaign.application.port.in.campaignImage.UpdateCampaignImageUseCase;
import com.kijinkai.domain.campaign.application.port.out.CampaignImagePersistencePort;
import com.kijinkai.domain.campaign.application.port.out.CampaignPersistencePort;
import com.kijinkai.domain.campaign.domain.exception.CampaignImageNotFoundException;
import com.kijinkai.domain.campaign.domain.exception.CampaignNotFoundException;
import com.kijinkai.domain.campaign.domain.factory.CampaignFactory;
import com.kijinkai.domain.campaign.domain.modal.*;
import com.kijinkai.domain.coupon.application.port.in.coupon.UpdateCouponUseCase;
import com.kijinkai.domain.coupon.application.port.out.CouponPersistencePort;
import com.kijinkai.domain.coupon.domain.exception.CouponNotFoundException;
import com.kijinkai.domain.coupon.domain.modal.Coupon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CampaignApplicationService implements CreateCampaignUseCase, GetCampaignUseCase, UpdateCampaignUseCase, DeleteCampaignUseCase {


    private final CampaignFactory campaignFactory;
    private final CampaignPersistencePort campaignPersistencePort;
    private final CampaignMapper campaignMapper;

    private final CouponPersistencePort couponPersistencePort;
    private final CampaignImagePersistencePort campaignImagePersistencePort;
    private final UpdateCouponUseCase updateCouponUseCase;

    private final UpdateCampaignImageUseCase updateCampaignImageUseCase;


    @Override
    @Transactional
    public CampaignResponseDto saveCampaign(UUID userAdminUuid, CampaignCreateRequestDto requestDto) {

        // 캠페인 생성 및 생성
        Campaign campaign = campaignFactory.createCampaign(userAdminUuid, requestDto);

        // 저장
        Campaign savedCampaign = campaignPersistencePort.saveCampaign(campaign);

        // 이미지 등록
        updateCampaignImageUseCase.addCampaignId(requestDto.getImageUuids(), savedCampaign);

        // 쿠폰 등록
        updateCouponUseCase.addCampaignUuid(requestDto.getCouponUuid(), savedCampaign.getCampaignUuid());


        return campaignMapper.toCreateResponse(savedCampaign);
    }


    @Override
    public CampaignResponseDto getDetailCampaign(UUID campaignUuid) {

        // 캠페인 조회
        Campaign campaign = findCampaignByCampaignUuid(campaignUuid);

        // 해당 캠페인의 쿠폰 조회
        Coupon coupon = null;
        if (campaign.getCampaignType() == CampaignType.COUPON) {
            couponPersistencePort.findByCampaignUuid(campaignUuid)
                    .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));
        }

        // 해당 캠페인의 메인 이미지 조회
        CampaignImage campaignImage = campaignImagePersistencePort.findByCampaignCampaignUuidAndImageType(campaignUuid, CampaignImageType.CONTENT)
                .orElse(null);


        return campaignMapper.toDetailResponse(campaign, campaignImage, coupon);
    }


    /*
    상태별 진행중 캠페인
     */
    @Override
    public Page<CampaignResponseDto> getCampaignsByStatus(CampaignStatus status, Pageable pageable) {

        // 진행중인 캠페인 조회
        Page<Campaign> campaigns = campaignPersistencePort.findAllByCampaignStatus(status, pageable);

        if (campaigns.isEmpty()) {
            return Page.empty(pageable);
        }


        List<Long> campaignIds = campaigns.map(Campaign::getCampaignId).toList();

        Map<Long, CampaignImage> imageMap = campaignImagePersistencePort.findAllByCampaignCampaignIdInAndImageType(campaignIds, CampaignImageType.THUMBNAIL)
                .stream()
                .collect(Collectors.toMap(
                        image -> image.getCampaign().getCampaignId(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));


        return campaigns.map(campaign -> {
            CampaignImage thumbnail = imageMap.get(campaign.getCampaignId());
            return campaignMapper.toThumbnailResponse(campaign, thumbnail);
        });

    }


    // page -> 타이틀, 상태별, 기간별

    @Override
    public Page<CampaignResponseDto> getCampaigns(String title, CampaignType type, CampaignStatus status,
                                                  LocalDate startDate, LocalDate endDate, Boolean featured, Integer minParticipants, Pageable pageable) {

        // 관리자 권한 검증 추가

        CampaignSearchCondition condition = CampaignSearchCondition.builder()
                .title(title)
                .type(type)
                .status(status)
                .searchStartDate(startDate)
                .searchEndDate(endDate)
                .featured(featured)
                .minParticipants(minParticipants)
                .build();

        // 캠페인 페이지 조회
        Page<Campaign> campaigns = campaignPersistencePort.searchCampaign(condition, pageable);

        if (campaigns.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> campaignIds = campaigns.map(Campaign::getCampaignId).toList();

        Map<Long, CampaignImage> imageMap = campaignImagePersistencePort
                .findAllByCampaignCampaignIdInAndImageType(campaignIds, CampaignImageType.THUMBNAIL)
                .stream()
                .collect(Collectors.toMap(
                        image -> image.getCampaign().getCampaignId(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));


        return campaigns.map(campaign -> {
            CampaignImage thumbnail = imageMap.get(campaign.getCampaignId());
            return campaignMapper.toThumbnailResponse(campaign, thumbnail);
        });
    }


    @Override
    @Transactional
    public CampaignResponseDto updateCampaign(Campaign campaign, CampaignUpdateRequestDto requestDto) {

        // 캠페인 변경 및 저장
        campaign.updateCampaign(requestDto);
        Campaign savedCampaign = campaignPersistencePort.saveCampaign(campaign);

        // 이미지 변경
        updateCampaignImageUseCase.updateCampaignImage(savedCampaign.getCampaignId(), requestDto.getDetachImageUuids(), requestDto.getAssignImageUuids());

        return campaignMapper.toUpdateResponse(campaign);
    }


    @Override
    @Transactional
    public void deleteCampaign(UUID campaignUuid) {

        Campaign campaign = findCampaignByCampaignUuid(campaignUuid);

        campaignPersistencePort.deleteCampaign(campaign);
    }


    // helper
    private CampaignImage findCampaignImageByCampaignUuidAndType(UUID campaignUuid, CampaignImageType type) {
        return campaignImagePersistencePort.findByCampaignCampaignUuidAndImageType(campaignUuid, type)
                .orElseThrow(() -> new CampaignImageNotFoundException("이미지를 찾을 수 없습니다."));
    }

    private Campaign findCampaignByCampaignUuid(UUID campaignUuid) {
        return campaignPersistencePort.findByCampaignUuid(campaignUuid)
                .orElseThrow(() -> new CampaignNotFoundException("캠페인을 찾을 수 없습니다."));
    }
}
