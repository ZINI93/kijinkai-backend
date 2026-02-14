package com.kijinkai.domain.coupon.domain.modal;

import com.kijinkai.domain.campaign.domain.modal.Campaign;
import com.kijinkai.domain.coupon.application.dto.request.CouponUpdateRequestDto;
import com.kijinkai.domain.coupon.domain.exception.CouponErrorCode;
import com.kijinkai.domain.coupon.domain.exception.CouponValidateException;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    private Long couponId;
    private UUID couponUuid;
    private UUID createdAdminUuid;
    private UUID campaignUuid;
    private String couponCode;
    private String couponName;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private Integer totalQuantity;
    private Integer issuedQuantity;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private CouponIssuedType couponIssuedType;
    private boolean active;


    // --- 검증.


    /**
     * 쿠폰 발행 검증
     */
    public void validateIssuable() {

        LocalDateTime now = LocalDateTime.now();

        if (this.validUntil.isBefore(now)) {
            throw new CouponValidateException(CouponErrorCode.EXPIRED);
        }

        if (!this.active) {
            throw new CouponValidateException(CouponErrorCode.INACTIVE);
        }

        if (this.totalQuantity != null && this.issuedQuantity >= this.totalQuantity) {
            throw new CouponValidateException(CouponErrorCode.SOLD_OUT);
        }
    }

    public void validateUsable() {

        LocalDateTime now = LocalDateTime.now();

        if (this.validFrom.isAfter(now)) {
            throw new CouponValidateException(CouponErrorCode.NOT_STARTED, this.validFrom);
        }

        if (!this.active) {
            throw new CouponValidateException(CouponErrorCode.INACTIVE);
        }
    }


    // -- 업데이트.


    public void increaseIssuedQuantity() {

        if (this.totalQuantity != null && this.issuedQuantity >= this.totalQuantity) {
            throw new CouponValidateException(CouponErrorCode.ISSUE_LIMIT_EXCEEDED);
        }

        this.issuedQuantity++;
    }


    /**
     * 쿠폰 활성화
     */
    public void active() {

        if (this.active) {
            return;
        }

        this.active = true;
    }

    /*
    캠페인 추가
     */

    public void addCampaignUuid(UUID campaignUuid){
        if (this.campaignUuid != null){
            throw new CouponValidateException(CouponErrorCode.COUPON_REGISTERED_IN_CAMPAIGN);
        }
        this.campaignUuid = campaignUuid;
    }

    /**
     * 쿠폰 업데이트
     *
     * @param requestDto
     */
    public void updateCoupon(CouponUpdateRequestDto requestDto) {

        if (!this.active) {
            throw new CouponValidateException(CouponErrorCode.INACTIVE);
        }

        this.campaignUuid = requestDto.getCampaignUuid();
        this.discountType = requestDto.getDiscountType();
        this.discountValue = requestDto.getDiscountValue();
        this.totalQuantity = requestDto.getTotalQuantity();
        this.minOrderAmount = requestDto.getMinOrderAmount();
        this.maxDiscountAmount = requestDto.getMaxDiscountAmount();
        this.validFrom = requestDto.getValidFrom();
        this.validUntil = requestDto.getValidUntil();

    }

}
