package com.kijinkai.domain.campaign.application.dto.response;

import com.kijinkai.domain.campaign.domain.modal.CampaignStatus;
import com.kijinkai.domain.campaign.domain.modal.CampaignType;
import com.kijinkai.domain.coupon.domain.modal.CouponIssuedType;
import com.kijinkai.domain.coupon.domain.modal.DiscountType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "캠페인 응답")
public class CampaignResponseDto {


    // 이미지관련
    private UUID mainImageUuid;
    private UUID thumbnailImageUuid;
    private String contentsImageUrl;
    private String thumbnailImageUrl;

    // 쿠폰관련
    private BigDecimal couponValue;
    private DiscountType discountType;
    private String couponCode;

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
    private boolean alwaysOn;
}
