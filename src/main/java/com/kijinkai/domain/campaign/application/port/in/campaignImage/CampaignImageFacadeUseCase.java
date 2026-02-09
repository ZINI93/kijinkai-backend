package com.kijinkai.domain.campaign.application.port.in.campaignImage;

import com.kijinkai.domain.campaign.application.dto.request.CampaignImageCreateRequestDto;
import com.kijinkai.domain.campaign.application.dto.response.CampaignImageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CampaignImageFacadeUseCase {

    CampaignImageResponseDto createCampaignImage(UUID userAdminUuid, CampaignImageCreateRequestDto requestDto);

    void deleteCampaignImages(UUID userAdminUuid, List<UUID> imageUuids);

    CampaignImageResponseDto getImageDetails(UUID userAdminUuid, UUID imageUuid);

    Page<CampaignImageResponseDto> getCampaignImages(UUID userAdminUuid, Pageable pageable);

}
