package com.kijinkai.domain.coupon.domain.modal;


import com.kijinkai.domain.coupon.domain.exception.UserCouponValidateException;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCoupon {

    private Long userCouponId;
    private UUID userCouponUuid;
    private UUID userUuid;
    private UUID couponUuid;
    private UUID usedOrderUuid;
    private BigDecimal usedDiscountAmount;
    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;
    private LocalDateTime usedAt;
    private UserCouponStatus userCouponStatus;
    private CouponIssuedType couponIssuedType;
    private Boolean reusable;
    private Long version;
    private Boolean locked;



    // 쿠폰 사용

    public void usedCoupon(BigDecimal usedDiscountAmount){

        LocalDateTime now = LocalDateTime.now();

        if (this.expiredAt.isBefore(now)){
            throw new UserCouponValidateException("사용기간이 만료되었습니다.");
        }

        if (this.userCouponStatus != UserCouponStatus.AVAILABLE){
            throw new UserCouponValidateException("이미 사용되었거나, 사용할 수 없는 상태입니다.");
        }

        this.usedDiscountAmount = usedDiscountAmount;
        this.usedAt = LocalDateTime.now();
        this.userCouponStatus = UserCouponStatus.USED;
    }
}
