package com.kijinkai.domain.campaign.domain.factory;

import com.kijinkai.domain.campaign.application.dto.request.CampaignCreateRequestDto;
import com.kijinkai.domain.campaign.domain.exception.CampaignValidateException;
import com.kijinkai.domain.campaign.domain.modal.Campaign;
import com.kijinkai.domain.campaign.domain.modal.CampaignStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CampaignFactory {


    public Campaign createCampaign(UUID userAdminUuid, CampaignCreateRequestDto requestDto) {

        validateRequest(requestDto);

        return Campaign.builder()
                .campaignUuid(UUID.randomUUID())
                .createdAdminUuid(userAdminUuid)
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .content(requestDto.getContent())
                .campaignType(requestDto.getCampaignType())
                .campaignStatus(CampaignStatus.READY)
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .featured(requestDto.isFeatured())
                .displayOrder(requestDto.getDisplayOrder())
                .alwaysOn(requestDto.isAlwaysOn())
                .build();


    }

    public void validateRequest(CampaignCreateRequestDto requestDto) {

        if (!requestDto.isAlwaysOn()) {
            if (requestDto.getStartDate() == null || requestDto.getEndDate() == null) {
                throw new CampaignValidateException("상시 캠페인이 아닌 경우에는 시작 날짜와 종료 날짜를 반드시 입력해야 합니다.");
            }




            LocalDateTime now = LocalDateTime.now();

            if (requestDto.getStartDate().isAfter(requestDto.getEndDate())) {
                throw new CampaignValidateException("종료 이후로 시작시간을 설정할 수 없습니다.");
            }

            if (requestDto.getStartDate().isAfter(now)) {
                throw new CampaignValidateException("시작시간은 현재 시간 보다 전으로 설정해주세요.");
            }

        }
    }
}
