package com.kijinkai.domain.coupon.adapter.out.repository.coupon;

import com.kijinkai.domain.coupon.adapter.out.entity.CouponJpaEntity;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.kijinkai.domain.coupon.adapter.out.entity.QCouponJpaEntity.*;


@RequiredArgsConstructor
public class CouponJpaEntityRepositoryImpl implements CouponJpaEntityRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CouponJpaEntity> searchCoupons(CouponSearchCondition condition, Pageable pageable) {

        // 1. 컨텐츠 조회

        List<CouponJpaEntity> content = queryFactory
                .selectFrom(couponJpaEntity)
                .where(
                        campaignUuidEq(condition.getCampaignUuid()),
                        couponCodeEq(condition.getCouponCode()),
                        activeEq(condition.getActive()),
                        totalQuantityBetween(condition.getMinTotalQuantity(), condition.getMaxTotalQuantity()),
                        validDate(condition.getValidFrom(), condition.getValidUntil())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .fetch();

        // 2. 카운트 조회
        JPAQuery<Long> countQuery = queryFactory
                .select(couponJpaEntity.count())
                .from(couponJpaEntity)
                .where(
                        campaignUuidEq(condition.getCampaignUuid()),
                        couponCodeEq(condition.getCouponCode()),
                        activeEq(condition.getActive()),
                        totalQuantityBetween(condition.getMinTotalQuantity(), condition.getMaxTotalQuantity()),
                        validDate(condition.getValidFrom(), condition.getValidUntil())
                );


        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


    //helper method

    private BooleanExpression campaignUuidEq(UUID campaignUuid) {
        return campaignUuid != null ? couponJpaEntity.campaignUuid.eq(campaignUuid) : null;
    }

    private BooleanExpression activeEq(Boolean active) {
        return active != null ? couponJpaEntity.active.eq(active) : null;
    }

    private BooleanExpression couponCodeEq(String coupon) {
        return coupon != null ? couponJpaEntity.couponCode.eq(coupon) : null;
    }


    private BooleanExpression totalQuantityBetween(Integer minTotalQuantity, Integer maxTotalQuantity) {

        if (minTotalQuantity == null && maxTotalQuantity == null) {
            return null;
        }

        if (maxTotalQuantity == null) {    // 작은거
            return couponJpaEntity.totalQuantity.goe(minTotalQuantity);
        }

        if (minTotalQuantity == null) {   // 큰거
            return couponJpaEntity.totalQuantity.loe(maxTotalQuantity);
        }

        return couponJpaEntity.totalQuantity.between(
                minTotalQuantity,
                maxTotalQuantity
        );
    }

    private BooleanExpression validDate(LocalDate validFrom, LocalDate validUntil) {

        if (validFrom == null && validUntil == null) {
            return null;
        }

        if (validUntil == null) {
            return couponJpaEntity.createdAt.goe(validFrom.atStartOfDay());
        }

        if (validFrom == null) {
            return couponJpaEntity.createdAt.loe(validUntil.atTime(LocalTime.MAX));
        }

        return couponJpaEntity.createdAt.between(
                validFrom.atStartOfDay(),
                validUntil.atTime(LocalTime.MAX)
        );
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        sort.stream().forEach(order ->

        {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String property = order.getProperty();

            switch (property) {
                case "createdAt":
                    orders.add(new OrderSpecifier<>(direction, couponJpaEntity.createdAt));
                    break;

                case "validUntil":
                    orders.add(new OrderSpecifier<>(direction, couponJpaEntity.validUntil));
                    break;

                case "totalQuantity":
                    orders.add(new OrderSpecifier<>(direction, couponJpaEntity.totalQuantity));
                    break;
            }

        });

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier<>(Order.DESC, couponJpaEntity.createdAt));
        }

        return orders.toArray(OrderSpecifier[]::new);

    }

}
