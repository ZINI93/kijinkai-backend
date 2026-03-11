package com.kijinkai.domain.coupon.application.servcie.issuance;

import com.kijinkai.domain.coupon.application.dto.request.CouponIssuanceRequestDto;
import com.kijinkai.domain.coupon.application.port.in.usercoupon.CreateUserCouponUseCase;
import com.kijinkai.domain.coupon.application.port.out.CouponPersistencePort;
import com.kijinkai.domain.coupon.domain.exception.CouponNotFoundException;
import com.kijinkai.domain.coupon.domain.modal.Coupon;
import com.kijinkai.domain.coupon.domain.modal.CouponIssuedType;
import com.kijinkai.domain.coupon.domain.modal.UserCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IssuanceTransactionManager {

    private final CouponPersistencePort couponPersistencePort;
    private final CreateUserCouponUseCase createUserCouponUseCase;


    /*
    쿠폰 UUID로 일반 쿠폰 배부
     */
    @Transactional
    public void processIssuance(UUID userUuid, UUID couponUuid) {
        Coupon coupon = couponPersistencePort.findByCouponUuid(couponUuid)
                .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));

        coupon.validateIssuable();
        coupon.increaseIssuedQuantity();

        couponPersistencePort.saveCoupon(coupon);
        createUserCouponUseCase.createUserCoupon(userUuid, couponUuid, coupon.getValidUntil(), coupon.getCouponIssuedType());
    }


    /*
     쿠폰 코드 발급 프로세스
     */
    @Transactional
    public void processIssuanceByCouponCode(UUID userUuid, CouponIssuanceRequestDto requestDto) {

        Coupon coupon = couponPersistencePort.findByCouponCode(requestDto.getCouponCode())
                .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));

        coupon.validateIssuable();
        coupon.increaseIssuedQuantity();

        couponPersistencePort.saveCoupon(coupon);
        createUserCouponUseCase.createUserCoupon(userUuid, coupon.getCouponUuid(), coupon.getValidUntil(), coupon.getCouponIssuedType());

    }

    /*
    활성화 중인 쿠폰을 상태 별로 한번에 배부
     */
    @Transactional
    public void processIssuanceByCouponType(UUID userUuid, CouponIssuedType type) {


        // 활성화 중인 유저 쿠폰을 탑입별로 불러온다
        List<Coupon> coupons = couponPersistencePort.findAllByCouponIssuedTypeAndActive(type, true);

        //쿠폰 검증 및 사용 수량 증가
        coupons.forEach(coupon -> {
            coupon.validateIssuable();
            coupon.increaseIssuedQuantity();
        });

        // 수량 저장
        List<Coupon> savedCoupons = couponPersistencePort.saveCoupons(coupons);

        //유저 쿠폰 생성
        savedCoupons.forEach(coupon -> createUserCouponUseCase.createUserCoupon(userUuid, coupon.getCouponUuid(), coupon.getValidUntil(), coupon.getCouponIssuedType())
        );
    }
}
