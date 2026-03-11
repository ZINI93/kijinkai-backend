package com.kijinkai.domain.delivery.adpater.out.persistence.repository;


import com.kijinkai.domain.delivery.adpater.out.persistence.entity.DeliveryJpaEntity;
import com.kijinkai.domain.delivery.domain.model.DeliveryStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.kijinkai.domain.customer.adapter.out.persistence.entity.QCustomerJpaEntity.*;
import static com.kijinkai.domain.delivery.adpater.out.persistence.entity.QDeliveryJpaEntity.*;
import static com.kijinkai.domain.order.adapter.out.persistence.entity.QOrderJpaEntity.orderJpaEntity;

@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryRepositoryCustom {


    private final JPAQueryFactory queryFactory;

    @Override
    public Page<DeliveryJpaEntity> searchDeliveries(DeliverySearchCondition condition, Pageable pageable) {

        List<DeliveryJpaEntity> content = queryFactory
                .selectFrom(deliveryJpaEntity)
                .leftJoin(customerJpaEntity).on(deliveryJpaEntity.customerUuid.eq(customerJpaEntity.customerUuid))
                .where(
                        nameCon(condition.getName()),
                        phoneNumberCon(condition.getPhoneNumber()),
                        deliveryStatusEq(condition.getStatus()),
                        dateBetween(condition.getStartDate(), condition.getEndDate())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(deliveryJpaEntity.createdAt.desc())
                .fetch();


        JPAQuery<Long> contentQuery = queryFactory
                .select(deliveryJpaEntity.count())
                .leftJoin(customerJpaEntity).on(deliveryJpaEntity.customerUuid.eq(customerJpaEntity.customerUuid))
                .where(
                        nameCon(condition.getName()),
                        phoneNumberCon(condition.getPhoneNumber()),
                        deliveryStatusEq(condition.getStatus()),
                        dateBetween(condition.getStartDate(), condition.getEndDate())
                );

        return PageableExecutionUtils.getPage(content, pageable, contentQuery::fetchOne);

    }


    private BooleanExpression nameCon(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }

        BooleanExpression firstName = customerJpaEntity.firstName.contains(name);
        BooleanExpression lastName = customerJpaEntity.lastName.contains(name);
        BooleanExpression fullName = customerJpaEntity.lastName.concat(customerJpaEntity.firstName).contains(name);

        return firstName.or(lastName).or(fullName);
    }

    private BooleanExpression phoneNumberCon(String phoneNumber) {
        if (!StringUtils.hasText(phoneNumber)) {
            return null;
        }
        return customerJpaEntity.phoneNumber.contains(phoneNumber);
    }

    private BooleanExpression deliveryStatusEq(DeliveryStatus status) {
        if (status == null) {
            return null;
        }
        return deliveryJpaEntity.deliveryStatus.eq(status);
    }


    private BooleanExpression dateBetween(LocalDate start, LocalDate end) {

        if (start != null && end != null) {
            return deliveryJpaEntity.createdAt.between(start.atStartOfDay(), end.atTime(LocalTime.MAX));
        }

        if (start != null) {
            return deliveryJpaEntity.createdAt.goe(start.atStartOfDay());
        }

        if (end != null) {
            return deliveryJpaEntity.createdAt.loe(end.atTime(LocalTime.MAX));
        }
        return null;
    }
}
