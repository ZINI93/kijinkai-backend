package com.kijinkai.domain.campaign.domain.modal;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CampaignParticipant {

    private Long campaignParticipantId;
    private UUID campaignParticipantUuid;
    private Long CampaignId;
    private UUID userUuid;
    private LocalDateTime participatedAt;
    private Boolean rewardGiven;
    private RewardType rewardType;

}
