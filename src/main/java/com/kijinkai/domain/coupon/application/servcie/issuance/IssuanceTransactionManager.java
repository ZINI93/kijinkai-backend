package com.kijinkai.domain.coupon.application.servcie.issuance;

import com.kijinkai.domain.coupon.application.dto.request.CouponIssuanceRequestDto;
import com.kijinkai.domain.coupon.application.port.in.usercoupon.CreateUserCouponUseCase;
import com.kijinkai.domain.coupon.application.port.out.CouponPersistencePort;
import com.kijinkai.domain.coupon.domain.exception.CouponNotFoundException;
import com.kijinkai.domain.coupon.domain.modal.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IssuanceTransactionManager {

    private final CouponPersistencePort couponPersistencePort;
    private final CreateUserCouponUseCase createUserCouponUseCase;


    @Transactional
    public void processIssuance(UUID userUuid, UUID couponUuid){
        Coupon coupon = couponPersistencePort.findByCouponUuid(couponUuid)
                .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));

        coupon.validateIssuable();
        coupon.increaseIssuedQuantity();

        couponPersistencePort.saveCoupon(coupon);
        createUserCouponUseCase.createUserCoupon(userUuid, couponUuid, coupon.getValidUntil() , coupon.getCouponIssuedType());
    }


    @Transactional
    public void processIssuanceByCouponCode(UUID userUuid, CouponIssuanceRequestDto requestDto){

        Coupon coupon = couponPersistencePort.findByCouponCode(requestDto.getCouponCode())
                .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));

        coupon.validateIssuable();
        coupon.increaseIssuedQuantity();

        couponPersistencePort.saveCoupon(coupon);
        createUserCouponUseCase.createUserCoupon(userUuid, coupon.getCouponUuid(), coupon.getValidUntil() , coupon.getCouponIssuedType());

    }
}
