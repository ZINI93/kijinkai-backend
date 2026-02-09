package com.kijinkai.domain.campaign.application.mapper;


import com.kijinkai.domain.campaign.application.dto.response.CampaignResponseDto;
import com.kijinkai.domain.campaign.domain.modal.Campaign;
import com.kijinkai.domain.campaign.domain.modal.CampaignImage;
import com.kijinkai.domain.coupon.domain.modal.Coupon;
import org.springframework.stereotype.Component;

@Component
public class CampaignMapper {

    public CampaignResponseDto toCreateResponse(Campaign campaign) {

        return CampaignResponseDto.builder()
                .title(campaign.getTitle())
                .description(campaign.getDescription())
                .content(campaign.getContent())
                .campaignType(campaign.getCampaignType())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .build();

    }

    public CampaignResponseDto toUpdateResponse(Campaign campaign) {

        return CampaignResponseDto.builder()
                .title(campaign.getTitle())
                .description(campaign.getDescription())
                .content(campaign.getContent())
                .campaignType(campaign.getCampaignType())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .build();

    }


    public CampaignResponseDto toDetailResponse(Campaign campaign, CampaignImage contentsImage, Coupon coupon) {

        return CampaignResponseDto.builder()
                .contentsImageUrl(contentsImage != null ? contentsImage.getImageUrl() : null)
                .campaignType(campaign.getCampaignType())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .title(campaign.getTitle())
                .description(campaign.getDescription())
                .content(campaign.getContent())
                .couponValue(coupon != null ? coupon.getDiscountValue() : null)
                .discountType(coupon != null ? coupon.getDiscountType() : null)
                .couponCode(coupon != null ? coupon.getCouponCode() : null)
                .build();

    }

    public CampaignResponseDto toThumbnailResponse(Campaign campaign, CampaignImage thumbnailImage) {

        return CampaignResponseDto.builder()
                .campaignUuid(campaign.getCampaignUuid())
                .thumbnailImageUrl(thumbnailImage != null ? thumbnailImage.getImageUrl() : null)
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .title(campaign.getTitle())
                .campaignType(campaign.getCampaignType())
                .campaignStatus(campaign.getCampaignStatus())
                .description(campaign.getDescription())
                .displayOrder(campaign.getDisplayOrder())
                .alwaysOn(campaign.isAlwaysOn())
                .build();

    }

}
