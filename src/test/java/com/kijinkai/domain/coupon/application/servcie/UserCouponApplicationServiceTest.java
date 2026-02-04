package com.kijinkai.domain.coupon.application.servcie;

import com.kijinkai.domain.coupon.application.mapper.UserCouponMapper;
import com.kijinkai.domain.coupon.application.port.out.CouponPersistencePort;
import com.kijinkai.domain.coupon.application.port.out.UserCouponPersistencePort;
import com.kijinkai.domain.coupon.domain.exception.CouponValidateException;
import com.kijinkai.domain.coupon.domain.factory.UserCouponFactory;
import com.kijinkai.domain.coupon.domain.modal.Coupon;
import com.kijinkai.domain.coupon.domain.modal.CouponIssuedType;
import com.kijinkai.domain.coupon.domain.modal.DiscountType;
import com.kijinkai.domain.coupon.domain.modal.UserCoupon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCouponApplicationServiceTest {

    @Mock private UserCouponPersistencePort userCouponPersistencePort;
    @Mock private UserCouponFactory userCouponFactory;
    @Mock private UserCouponMapper userCouponMapper;
    @Mock private CouponPersistencePort couponPersistencePort;

    @InjectMocks
    private UserCouponApplicationService userCouponApplicationService;

    private UUID userUuid;
    private UUID couponUuid;
    private UUID userCouponUuid;

    @BeforeEach
    void setUp() {
        userUuid = UUID.randomUUID();
        couponUuid = UUID.randomUUID();
        userCouponUuid = UUID.randomUUID();
    }

    // --- [1. 쿠폰 획득 테스트] ---

    @Test
    @DisplayName("쿠폰 획득 성공: 중복되지 않은 경우 쿠폰을 정상 발급한다.")
    void createUserCoupon_Success() {
        // given
        LocalDateTime expired = LocalDateTime.now().plusDays(7);
        UserCoupon mockUserCoupon = Mockito.mock(UserCoupon.class);

        when(userCouponPersistencePort.existsByUserUuidAndCouponUuid(userUuid, couponUuid)).thenReturn(false);
        when(userCouponFactory.createUserCoupon(any(), any(), any(), any())).thenReturn(mockUserCoupon);
        when(userCouponPersistencePort.saveUserCoupon(any())).thenReturn(mockUserCoupon);

        // when
        UserCoupon result = userCouponApplicationService.createUserCoupon(userUuid, couponUuid, expired, CouponIssuedType.ADMIN);

        // then
        assertThat(result).isNotNull();
        verify(userCouponPersistencePort).saveUserCoupon(any());
    }

    @Test
    @DisplayName("쿠폰 획득 실패: 이미 발급받은 쿠폰이면 예외를 던진다.")
    void createUserCoupon_Fail_Duplicate() {
        // given
        when(userCouponPersistencePort.existsByUserUuidAndCouponUuid(userUuid, couponUuid)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userCouponApplicationService.createUserCoupon(userUuid, couponUuid, null, null))
                .isInstanceOf(CouponValidateException.class)
                .hasMessageContaining("이미 발급받은 쿠폰 입니다.");
    }

    // --- [2. 경계값 및 방어적 테스트] ---

    @Test
    @DisplayName("상세 조회 실패: 입력값이 null인 경우 예외를 발생시킨다.")
    void getMyCouponInfo_Fail_NullInput() {
        // when & then
        assertThatThrownBy(() -> userCouponApplicationService.getMyCouponInfo(null, userCouponUuid))
                .isInstanceOf(CouponValidateException.class);

        assertThatThrownBy(() -> userCouponApplicationService.getMyCouponInfo(userUuid, null))
                .isInstanceOf(CouponValidateException.class);
    }

    // --- [3. 쿠폰 사용 및 할인 로직 테스트] ---

    @Test
    @DisplayName("쿠폰 사용 성공: 퍼센트 할인 및 최대 할인 한도를 적용한다.")
    void useCoupon_Success_PercentDiscount() {
        // given
        BigDecimal orderAmount = new BigDecimal("100000"); // 10만원
        UserCoupon userCoupon = Mockito.mock(UserCoupon.class);

        // 10% 할인, 최대 5000원 제한 쿠폰 설정
        Coupon coupon = createMockCoupon(DiscountType.PERCENT, new BigDecimal("10"), new BigDecimal("5000"), new BigDecimal("10000"));

        when(userCouponPersistencePort.findByUserUuidAndUserCouponUuid(userUuid, userCouponUuid)).thenReturn(Optional.of(userCoupon));
        when(couponPersistencePort.findByCouponUuid(any())).thenReturn(Optional.of(coupon));
        when(userCouponPersistencePort.saveUserCoupon(any())).thenReturn(userCoupon);
        when(userCoupon.getUserCouponUuid()).thenReturn(userCouponUuid);

        // when
        BigDecimal discount = userCouponApplicationService.useCoupon(userUuid, userCouponUuid, orderAmount);

        // then
        // 10만원의 10%는 1만원이지만, 최대 한도인 5천원만 할인되어야 함
        verify(userCoupon).usedCoupon(new BigDecimal("5000"));
//        assertThat(discount).isEqualTo(orderAmount.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP));
    }

    @Test
    @DisplayName("쿠폰 사용 실패: 최소 주문 금액 미달 시 예외를 발생시킨다.")
    void useCoupon_Fail_MinOrderAmount() {
        // given
        BigDecimal orderAmount = new BigDecimal("5000");
        Coupon coupon = createMockCoupon(DiscountType.FIXED, new BigDecimal("1000"), null, new BigDecimal("10000"));
        UserCoupon userCoupon = Mockito.mock(UserCoupon.class);

        when(userCouponPersistencePort.findByUserUuidAndUserCouponUuid(userUuid, userCouponUuid)).thenReturn(Optional.of(userCoupon));
        when(couponPersistencePort.findByCouponUuid(any())).thenReturn(Optional.of(coupon));

        // when & then
        assertThatThrownBy(() -> userCouponApplicationService.useCoupon(userUuid, userCouponUuid, orderAmount))
                .isInstanceOf(CouponValidateException.class)
                .hasMessageContaining("최소 주문 금액을 충족하지 않습니다.");
    }

    // --- [Helper Method] ---

    private Coupon createMockCoupon(DiscountType type, BigDecimal value, BigDecimal maxDiscount, BigDecimal minOrder) {
        return Coupon.builder()
                .couponUuid(UUID.randomUUID())
                .discountType(type)
                .discountValue(value)
                .maxDiscountAmount(maxDiscount)
                .minOrderAmount(minOrder)
                .isActive(true)
                .validFrom(LocalDateTime.now().minusDays(1))
                .validUntil(LocalDateTime.now().plusDays(1))
                .build();
    }
}