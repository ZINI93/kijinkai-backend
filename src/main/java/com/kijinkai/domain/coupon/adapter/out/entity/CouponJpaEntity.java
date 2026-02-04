package com.kijinkai.domain.coupon.adapter.out.entity;

import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.coupon.domain.modal.CouponIssuedType;
import com.kijinkai.domain.coupon.domain.modal.DiscountType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;



@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "coupons")
@Entity
public class CouponJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id", nullable = false , unique = true)
    private Long couponId;

    @Comment("쿠폰 UUID 식별자")
    @Column(name = "coupon_uuid", nullable = false, updatable = false, unique = true)
    private UUID couponUuid;

    @Comment("쿠폰 생성한 관리자의 식별자")
    @Column(name = "created_admin_uuid)", nullable = false, updatable = false)
    private UUID createdAdminUuid;

    @Comment("캠페인 연관 UUID 식별자")
    @Column(name = "campaign_uuid")
    private UUID campaignUuid;

    @Comment("쿠폰 코드")
    @Column(name = "coupon_code", nullable = false, updatable = false, unique = true)
    private String couponCode;

    @Comment("쿠폰 이름")
    @Column(name = "coupon_name", nullable = false)
    private String couponName;


    @Comment("할인종류 퍼센트/금액")
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, updatable = false)
    private DiscountType discountType;


    @Comment("발행 쿠폰의 타입")
    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_issued_type", nullable = false, updatable = false)
    private CouponIssuedType couponIssuedType;

    @Comment("할인 수치")
    @Column(name = "discount", nullable = false)
    private BigDecimal discountValue;

    @Comment("최소 주문 금액 제한")
    @Column(name = "min_order_amount", nullable = false)
    private BigDecimal minOrderAmount;

    @Comment("최대 할인 갭")
    @Column(name = "max_discount_amount")
    private BigDecimal maxDiscountAmount;

    @Comment("총 발행 수량")
    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Comment("현재 발행 수량")
    @Column(name = "issued_quantity", nullable = false)
    private Integer issuedQuantity;

    @Comment("사용 시작 가능 일")
    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Comment("사용 만료일")
    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;

    @Comment("사용 여부")
    @Column(name = "active", nullable = false)
    private boolean active;

}
