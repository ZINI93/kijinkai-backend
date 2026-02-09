package com.kijinkai.domain.campaign.application.servcie.facade;

import com.kijinkai.domain.campaign.application.dto.request.CampaignImageCreateRequestDto;
import com.kijinkai.domain.campaign.application.dto.response.CampaignImageResponseDto;
import com.kijinkai.domain.campaign.application.mapper.CampaignImageMapper;
import com.kijinkai.domain.campaign.application.port.in.campaignImage.CampaignImageFacadeUseCase;
import com.kijinkai.domain.campaign.application.port.in.campaignImage.CreateCampaignImageUseCase;
import com.kijinkai.domain.campaign.application.port.in.campaignImage.DeleteCampaignImageUseCase;
import com.kijinkai.domain.campaign.application.port.in.campaignImage.GetCampaignImageUseCase;
import com.kijinkai.domain.campaign.domain.modal.CampaignImage;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import com.kijinkai.domain.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class CampaignImageFacade implements CampaignImageFacadeUseCase {


    private final UserPersistencePort userPersistencePort;
    private final GetCampaignImageUseCase getCampaignImageUseCase;
    private final CreateCampaignImageUseCase createCampaignImageUseCase;
    private final DeleteCampaignImageUseCase deleteCampaignImageUseCase;
    private final CampaignImageMapper campaignImageMapper;

    @Override
    public CampaignImageResponseDto createCampaignImage(UUID userAdminUuid, CampaignImageCreateRequestDto requestDto){

        // 관리자 검증
        User userAdmin = userPersistencePort.findByUserUuid(userAdminUuid)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
        userAdmin.validateAdminRole();

        CampaignImage savedCampaignImage = createCampaignImageUseCase.saveCampaignImage(userAdminUuid, requestDto);

        return campaignImageMapper.toCreateResponse(savedCampaignImage);
    }


    @Override
    public void deleteCampaignImages(UUID userAdminUuid, List<UUID> imageUuids){

        // 관리자 검증
        User userAdmin = userPersistencePort.findByUserUuid(userAdminUuid)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
        userAdmin.validateAdminRole();

        deleteCampaignImageUseCase.deleteImages(imageUuids);

    }

    @Override
    public CampaignImageResponseDto getImageDetails(UUID userAdminUuid, UUID imageUuid){

        User userAdmin = userPersistencePort.findByUserUuid(userAdminUuid)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
        userAdmin.validateAdminRole();

        CampaignImage imageDetails = getCampaignImageUseCase.getImageDetails(imageUuid);

        return campaignImageMapper.toDetailResponse(imageDetails);

    }

    @Override
    public Page<CampaignImageResponseDto> getCampaignImages(UUID userAdminUuid, Pageable pageable){
        User userAdmin = userPersistencePort.findByUserUuid(userAdminUuid)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
        userAdmin.validateAdminRole();


        Page<CampaignImage> images = getCampaignImageUseCase.getImages(pageable);

        return images.map(campaignImageMapper::toDetailResponse);
    }

}
