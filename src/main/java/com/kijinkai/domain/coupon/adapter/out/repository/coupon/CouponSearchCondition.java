package com.kijinkai.domain.coupon.adapter.out.repository.coupon;

import com.kijinkai.domain.coupon.domain.modal.DiscountType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
public class CouponSearchCondition {

    // 검색 필터
    private UUID campaignUuid;
    private String couponCode;
    private DiscountType discountType;

    // 수량 관련 (특정 수량 이상/이하 검색이 필요할 수 있음)
    private Integer minTotalQuantity;
    private Integer maxTotalQuantity;

    // 기간 검색 (시작/종료일이 특정 범위에 걸쳐 있는지)
    private LocalDate validFrom;
    private LocalDate validUntil;

    // 상태
    private Boolean active; // null이면 전체, true/false 필터링

}
