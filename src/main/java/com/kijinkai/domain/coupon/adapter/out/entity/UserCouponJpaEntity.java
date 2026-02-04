package com.kijinkai.domain.coupon.adapter.out.entity;

import com.kijinkai.domain.coupon.domain.modal.CouponIssuedType;
import com.kijinkai.domain.coupon.domain.modal.UserCouponStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_coupons")
@Entity
public class UserCouponJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    private Long userCouponId;


    @Comment("발급받은 쿠폰 식별자")
    @Column(name = "user_coupon_uuid", nullable = false, updatable = false)
    private UUID userCouponUuid;

    @Comment("발급 유저 식별자")
    @Column(name = "user_uuid", nullable = false, updatable = false)
    private UUID userUuid;

    @Comment("원본 쿠폰 식별자")
    @Column(name = "coupon_uuid", nullable = false, updatable = false)
    private UUID couponUuid;

    @Comment("쿠폰이 사용된 주문 식별자")
    @Column(name = "used_order_uuid")
    private UUID usedOrderUuid;

    @Comment("실제 적용된 할인 금액")
    @Column(name = "used_discount_amount", precision = 15, scale = 2)
    private BigDecimal usedDiscountAmount;

    @Comment("쿠폰 발급 일시")
    @Column(name = "issued_at", nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    @Comment("쿠폰 만료 예정 일시")
    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Comment("쿠폰 실제 사용 일시")
    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Enumerated(EnumType.STRING)
    @Comment("쿠폰 상태 (사용가능, 사용완료, 만료 등)")
    @Column(name = "user_coupon_status", nullable = false)
    private UserCouponStatus userCouponStatus;

    @Enumerated(EnumType.STRING)
    @Comment("쿠폰 발급 유형")
    @Column(name = "coupon_issued_type", nullable = false)
    private CouponIssuedType couponIssuedType;

    @Comment("쿠폰 재사용 가능 여부")
    @Column(name = "reusable", nullable = false)
    private Boolean reusable;

    @Version
    @Comment("낙관적 잠금을 위한 버전")
    @Column(name = "version")
    private Long version;

    @Comment("수정 시 잠금 여부")
    @Column(name = "is_locked", nullable = false)
    private Boolean locked;
}