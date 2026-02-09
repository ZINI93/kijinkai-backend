package com.kijinkai.domain.campaign.application.port.in.campaign;

import com.kijinkai.domain.campaign.application.dto.response.CampaignResponseDto;
import com.kijinkai.domain.campaign.domain.modal.CampaignStatus;
import com.kijinkai.domain.campaign.domain.modal.CampaignType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface GetCampaignUseCase {

    CampaignResponseDto getDetailCampaign(UUID campaignUuid);

    Page<CampaignResponseDto> getCampaigns(String title, CampaignType type, CampaignStatus status,
                                           LocalDate startDate, LocalDate endDate, Boolean featured, Integer minParticipants, Pageable pageable);

    Page<CampaignResponseDto> getCampaignsByStatus(CampaignStatus status, Pageable pageable);
}
