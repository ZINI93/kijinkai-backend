package com.kijinkai.domain.campaign.domain.modal;

import com.kijinkai.domain.campaign.application.dto.request.CampaignUpdateRequestDto;
import com.kijinkai.domain.campaign.domain.exception.CampaignValidateException;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Campaign {

    private Long campaignId;
    private UUID campaignUuid;
    private UUID createdAdminUuid;
    private String title;
    private String description;
    private String content;

    private CampaignType campaignType;
    private CampaignStatus campaignStatus;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Boolean featured;
    private int displayOrder;

    private boolean alwaysOn;
    private int participantCount;



    public void updateCampaign(CampaignUpdateRequestDto requestDto){

        if (this.campaignStatus == CampaignStatus.PROGRESS){
            throw new CampaignValidateException("진행 중인 캠페인은 변경할수 없습니다.");
        }

        this.title = requestDto.getTitle();
        this.description = requestDto.getDescription();
        this.content = requestDto.getContent();
        this.campaignType = requestDto.getCampaignType();
        this.startDate = requestDto.getStartDate();
        this.endDate = requestDto.getEndDate();
        this.featured = requestDto.isFeatured();
        this.displayOrder = requestDto.getDisplayOrder();
    }
}
