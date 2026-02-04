package com.kijinkai.domain.campaign.domain.modal;

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
    private String title;
    private String description;
    private String content;

    private CampaignType campaignType;
    private CampaignStatus campaignStatus;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Boolean featured;
    private int displayOrder;


    private int participantCount;

}
