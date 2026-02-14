package com.kijinkai.domain.coupon.domain.exception;

import lombok.Getter;

@Getter
public enum CouponErrorCode {

    EXPIRED("C001", "기간이 지난 쿠폰은 발행할 수 없습니다."),
    INACTIVE("C002", "비활성화 쿠폰은 발행할 수 없습니다."),
    SOLD_OUT("C003", "준비된 쿠폰 수량이 모두 소진되었습니다."),
    CANNOT_DELETE_ACTIVE_COUPON("C004", "활성화 중인 쿠폰은 삭제할 수 없습니다."),
    ISSUE_LIMIT_EXCEEDED("C005", "발행 가능 수량을 초과하여 더 이상 증가시킬 수 없습니다."),
    COUPON_REGISTERED_IN_CAMPAIGN("C006", "현재 캠페인이 등록된 쿠폰 입니다."),
    NOT_STARTED("C007", "이 쿠폰은 %s 이후부터 사용이 가능합니다."),
    INVALID_DATE_RANGE("C008", "시작일은 종료일 보다 전이여야 합니다."),
    START_DATE_BEFORE_NOW("C009", "쿠폰 시작일은 현재 시간보다 이전일 수 없습니다."),
    END_DATE_BEFORE_NOW("C010", "쿠폰 종료일은 과거로 설정할 수 없습니다."),
    MAX_DISCOUNT_AMOUNT_REQUIRED("C011", "정률 할인 쿠폰은 최대 할인 금액을 설정해야 합니다."),
    INVALID_DISCOUNT_RATE_RANGE("C012", "할인율은 1 이상 100 이하로 설정해야 합니다."),
    INVALID_DISCOUNT_AMOUNT("C013", "할인 금액은 0보다 커야 합니다."),
    COUPON_ALREADY_ISSUED("C014", "해당 쿠폰은 이미 발급되었습니다."),
    COUPON_MIN_ORDER_AMOUNT_NOT_MET("C015", "최소 주문 금액을 충족하지 않습니다.");


    public final String code;
    private final String message;

    CouponErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getFormattedMessage(Object... args) {
        return String.format(this.message, args);
    }
    }
