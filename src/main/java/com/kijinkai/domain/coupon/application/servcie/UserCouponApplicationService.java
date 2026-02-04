package com.kijinkai.domain.coupon.application.servcie;

import com.kijinkai.domain.coupon.application.dto.response.UserCouponResponseDto;
import com.kijinkai.domain.coupon.application.mapper.UserCouponMapper;
import com.kijinkai.domain.coupon.application.port.in.usercoupon.CreateUserCouponUseCase;
import com.kijinkai.domain.coupon.application.port.in.usercoupon.DeleteUserCouponUseCase;
import com.kijinkai.domain.coupon.application.port.in.usercoupon.GetUserCouponUseCase;
import com.kijinkai.domain.coupon.application.port.in.usercoupon.UpdateUserCouponUseCase;
import com.kijinkai.domain.coupon.application.port.out.CouponPersistencePort;
import com.kijinkai.domain.coupon.application.port.out.UserCouponPersistencePort;
import com.kijinkai.domain.coupon.domain.exception.CouponNotFoundException;
import com.kijinkai.domain.coupon.domain.exception.CouponValidateException;
import com.kijinkai.domain.coupon.domain.exception.UserCouponNotFoundException;
import com.kijinkai.domain.coupon.domain.factory.UserCouponFactory;
import com.kijinkai.domain.coupon.domain.modal.Coupon;
import com.kijinkai.domain.coupon.domain.modal.CouponIssuedType;
import com.kijinkai.domain.coupon.domain.modal.DiscountType;
import com.kijinkai.domain.coupon.domain.modal.UserCoupon;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserCouponApplicationService implements CreateUserCouponUseCase, GetUserCouponUseCase, UpdateUserCouponUseCase, DeleteUserCouponUseCase {

    private final UserCouponPersistencePort userCouponPersistencePort;
    private final UserCouponFactory userCouponFactory;
    private final UserCouponMapper userCouponMapper;

    private final UserPersistencePort userPersistencePort;
    private final CouponPersistencePort couponPersistencePort;


    /*
    - 쿠폰 흭득
     */
    @Override
    @Transactional
    public UserCoupon createUserCoupon(UUID userUuid, UUID couponUuid, LocalDateTime expired, CouponIssuedType type) {

        // 중복 발행 검증
        Boolean existsByUserCoupon = userCouponPersistencePort.existsByUserUuidAndCouponUuid(userUuid, couponUuid);
        if (existsByUserCoupon) {
            throw new CouponValidateException("이미 발급받은 쿠폰 입니다.");
        }

        // 생성
        UserCoupon userCoupon = userCouponFactory.createUserCoupon(userUuid, couponUuid, expired, type);

        // 저장
        return userCouponPersistencePort.saveUserCoupon(userCoupon);
    }


    /*
    -  유저가 가지고 있는 쿠폰 전체조회
    */
    @Override
    public Page<UserCouponResponseDto> getMyCoupons(UUID userUuid, Pageable pageable) {

        Page<UserCoupon> userCoupons = userCouponPersistencePort.findAllByUserUuid(userUuid, pageable);

        List<UUID> couponUuids = userCoupons.map(UserCoupon::getCouponUuid).toList();
        Map<UUID, Coupon> couponMap = couponPersistencePort.findAllByCouponUuids(couponUuids).stream()
                .collect(Collectors.toMap(Coupon::getCouponUuid, Function.identity()));

        return userCoupons.map(userCoupon -> {
            Coupon coupon = couponMap.get(userCoupon.getCouponUuid());
            return userCouponMapper.toResponse(userCoupon,coupon);
        });
    }

    // 쿠폰 할인 금액 조회
    @Override

    public BigDecimal discountValue(UUID userUuid, UUID userCouponUuid, BigDecimal orderAmount){

        // 유저 쿠폰 조회
        UserCoupon userCoupon = findUserCouponByUserUuidAndUserCouponUuid(userUuid, userCouponUuid);

        // 쿠폰 조회
        Coupon coupon = couponPersistencePort.findByCouponUuid(userCoupon.getCouponUuid())
                .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));

        // 검증
        validateCouponPolicy(coupon, orderAmount);

        //계산
        return calculateDiscountAmount(coupon, orderAmount);
    }



    @Override
    public UserCouponResponseDto getMyCouponInfo(UUID userUuid, UUID userCouponUuid) {
        if (userUuid == null || userCouponUuid == null) {
            throw new CouponValidateException("유저의 쿠폰을 찾을 수 없습니다.");
        }

        // 유저 쿠폰 조회
        UserCoupon userCoupon = findUserCouponByUserUuidAndUserCouponUuid(userUuid, userCouponUuid);


        // 쿠폰 조회
        Coupon coupon = couponPersistencePort.findByCouponUuid(userCoupon.getCouponUuid())
                .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));


        return userCouponMapper.toResponse(userCoupon, coupon);

    }




    // 쿠폰 사용
    @Override
    @Transactional
    public void useCoupon(UUID userUuid, UUID userCouponUuid, BigDecimal discountAmount) {

        // 유저 쿠폰 조회
        UserCoupon userCoupon = findUserCouponByUserUuidAndUserCouponUuid(userUuid, userCouponUuid);

        // 상태변경 및 추가
        userCoupon.usedCoupon(discountAmount);

        userCouponPersistencePort.saveUserCoupon(userCoupon);

    }



    /*
    쿠폰 삭제
     */
    @Override
    public void deleteCoupon(UUID userUuid, UUID userCouponUuid) {
        UserCoupon userCoupon = findUserCouponByUserUuidAndUserCouponUuid(userUuid, userCouponUuid);
        userCouponPersistencePort.deleteUserCoupon(userCoupon);
    }

    // helper

    private void validateCouponPolicy(Coupon coupon, BigDecimal orderAmount){
        LocalDateTime now = LocalDateTime.now();

        if (!coupon.isActive()) {
            throw new CouponValidateException("현재 비활성화된 쿠폰입니다.");
        }
        if (coupon.getMinOrderAmount().compareTo(orderAmount) > 0) {
            throw new CouponValidateException("최소 주문 금액을 충족하지 않습니다.");
        }
        if (coupon.getValidFrom().isAfter(now)) {
            throw new CouponValidateException("아직 사용 기간이 아닙니다.");
        }
        if (coupon.getValidUntil().isBefore(now)) {
            throw new CouponValidateException("사용 기간이 만료된 쿠폰입니다.");
        }
    }

    private BigDecimal calculateDiscountAmount(Coupon coupon, BigDecimal orderAmount){

        BigDecimal discountAmount;

        if (coupon.getDiscountType() == DiscountType.PERCENT) {
            // 할인액 = 주문금액 * (할인율 / 100)
            discountAmount = orderAmount.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);

            // 최대 할인 한도 적용
            if (coupon.getMaxDiscountAmount() != null && discountAmount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                discountAmount = coupon.getMaxDiscountAmount();
            }
        } else {
            // 고정 할인
            discountAmount = coupon.getDiscountValue();

        }

        // 할인액이 주문금액보다 클 수는 없음 (무료 처리)
        return discountAmount.min(orderAmount);

    }


    private UserCoupon findUserCouponByUserUuidAndUserCouponUuid(UUID userUuid, UUID userCouponUuid) {
        return userCouponPersistencePort.findByUserUuidAndUserCouponUuid(userUuid, userCouponUuid)
                .orElseThrow(() -> new UserCouponNotFoundException("유저 쿠폰을 찾을 수 없습니다."));
    }


}
