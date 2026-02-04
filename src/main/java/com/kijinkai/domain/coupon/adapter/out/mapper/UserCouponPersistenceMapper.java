package com.kijinkai.domain.coupon.adapter.out.mapper;


import com.kijinkai.domain.coupon.adapter.out.entity.UserCouponJpaEntity;
import com.kijinkai.domain.coupon.domain.modal.UserCoupon;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserCouponPersistenceMapper {

    UserCoupon toUserCoupon(UserCouponJpaEntity userCouponJpaEntity);
    UserCouponJpaEntity toUserCouponJapEntity(UserCoupon userCoupon);


    List<UserCoupon> toUserCouponList(List<UserCouponJpaEntity> userCouponJpaEntities);
    List<UserCouponJpaEntity> toUserCouponJpaEntityList(List<UserCoupon> userCoupons);

}
