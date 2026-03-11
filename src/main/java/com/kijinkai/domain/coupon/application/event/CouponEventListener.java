package com.kijinkai.domain.coupon.application.event;


import com.kijinkai.domain.coupon.application.port.in.coupon.UpdateCouponUseCase;
import com.kijinkai.domain.user.domain.event.UserResistorEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class CouponEventListener {


    private final UpdateCouponUseCase updateCouponUseCase;


    @EventListener
    public void handleUserResistedEvent(UserResistorEvent event){
        updateCouponUseCase.issuanceBySignUpCoupon(event.getUser().getUserUuid());
    }
}
