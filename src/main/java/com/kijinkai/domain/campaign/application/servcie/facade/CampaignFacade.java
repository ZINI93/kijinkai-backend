package com.kijinkai.domain.campaign.application.servcie.facade;


import com.kijinkai.domain.campaign.application.dto.request.CampaignCreateRequestDto;
import com.kijinkai.domain.campaign.application.dto.request.CampaignUpdateRequestDto;
import com.kijinkai.domain.campaign.application.dto.response.CampaignResponseDto;
import com.kijinkai.domain.campaign.application.port.in.campaign.CampaignFacadeUseCase;
import com.kijinkai.domain.campaign.application.port.in.campaign.CreateCampaignUseCase;
import com.kijinkai.domain.campaign.application.port.in.campaign.DeleteCampaignUseCase;
import com.kijinkai.domain.campaign.application.port.in.campaign.UpdateCampaignUseCase;
import com.kijinkai.domain.campaign.application.port.out.CampaignPersistencePort;
import com.kijinkai.domain.campaign.domain.exception.CampaignNotFoundException;
import com.kijinkai.domain.campaign.domain.modal.Campaign;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import com.kijinkai.domain.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class CampaignFacade implements CampaignFacadeUseCase {

    private final UserPersistencePort userPersistencePort;
    private final CreateCampaignUseCase createCampaignUseCase;
    private final UpdateCampaignUseCase updateCampaignUseCase;
    private final CampaignPersistencePort campaignPersistencePort;
    private final DeleteCampaignUseCase deleteCampaignUseCase;

    @Override
    public CampaignResponseDto createCampaign(UUID userAdminUuid, CampaignCreateRequestDto requestDto) {

        // 관리자 검증
        User userAdmin = userPersistencePort.findByUserUuid(userAdminUuid)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
        userAdmin.validateAdminRole();

        return createCampaignUseCase.saveCampaign(userAdminUuid, requestDto);
    }


    @Override
    public CampaignResponseDto updateCampaign(UUID userAdminUuid, UUID campaignUuid, CampaignUpdateRequestDto requestDto) {

        // 관리자 검증
        User userAdmin = userPersistencePort.findByUserUuid(userAdminUuid)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
        userAdmin.validateAdminRole();

        //캠페인 조회
        Campaign campaign = campaignPersistencePort.findByCampaignUuid(campaignUuid)
                .orElseThrow(() -> new CampaignNotFoundException("캠페인을 찾을 수 없습니다."));

        return updateCampaignUseCase.updateCampaign(campaign, requestDto);

    }

    @Override
    public void deleteCampaign(UUID userAdminUuid, UUID campaignUuid){
        // 관리자 검증
        User userAdmin = userPersistencePort.findByUserUuid(userAdminUuid)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
        userAdmin.validateAdminRole();

        deleteCampaignUseCase.deleteCampaign(campaignUuid);
    }


}
