package com.kijinkai.domain.campaign.domain.modal;

import lombok.*;

import java.util.UUID;


@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CampaignImage {

    private Long campaignImageId;
    private UUID campaignImageUuid;

    private Long campaignId;

    private String imageUrl;
    private String imageType;
    private int displayOrder;
    private String altText;
}
