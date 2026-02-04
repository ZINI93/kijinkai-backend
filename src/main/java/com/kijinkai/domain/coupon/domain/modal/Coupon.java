package com.kijinkai.domain.coupon.domain.modal;

import com.kijinkai.domain.coupon.application.dto.request.CouponUpdateRequestDto;
import com.kijinkai.domain.coupon.domain.exception.CouponValidateException;
import lombok.*;

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
            throw new CouponValidateException("기간이 지난 쿠폰은 발행할 수 없습니다.");
        }

        if (!this.active) {
            throw new CouponValidateException("비활성화 쿠폰은 발행할 수 없습니다.");
        }

        if (this.totalQuantity != null && this.issuedQuantity >= this.totalQuantity) {
            throw new CouponValidateException("준비된 쿠폰 수량이 모두 소진되었습니다.");
        }
    }

    public void validateUsable() {

        LocalDateTime now = LocalDateTime.now();

        if (this.validFrom.isAfter(now)) {
            throw new CouponValidateException("이 쿠폰은" + validFrom + "이후 부터 사용이 가능합니다.");
        }

        if (!this.active) {
            throw new CouponValidateException("비활성화 쿠폰은 발행할 수 없습니다.");
        }
    }


    // -- 업데이트.


    public void increaseIssuedQuantity() {

        if (this.totalQuantity != null && this.issuedQuantity >= this.totalQuantity) {
            throw new CouponValidateException("발행 가능 수량을 초과하여 더 이상 증가시킬 수 없습니다.");
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


    /**
     * 쿠폰 업데이트
     *
     * @param requestDto
     */
    public void updateCoupon(CouponUpdateRequestDto requestDto) {

        if (!this.active) {
            throw new CouponValidateException("비활성화 상태에서만 수정할 수 있습니다.");
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
