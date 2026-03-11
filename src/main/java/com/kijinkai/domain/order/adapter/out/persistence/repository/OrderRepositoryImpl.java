package com.kijinkai.domain.order.adapter.out.persistence.repository;

import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderStatus;
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
import static com.kijinkai.domain.order.adapter.out.persistence.entity.QOrderJpaEntity.*;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<OrderJpaEntity> searchOrders(OrderSearchCondition condition, Pageable pageable) {

        List<OrderJpaEntity> content = queryFactory
                .selectFrom(orderJpaEntity)
                .leftJoin(customerJpaEntity).on(orderJpaEntity.customerUuid.eq(customerJpaEntity.customerUuid))
                .where(
                        orderCodeCon(condition.orderCode),
                        nameCon(condition.name),
                        orderStateEq(condition.getOrderStatus()),
                        dateBetween(condition.getStartDate(), condition.getEndDate())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderJpaEntity.createdAt.desc())
                .fetch();


        JPAQuery<Long> countQuery = queryFactory
                .select(orderJpaEntity.count())
                .from(orderJpaEntity)
                .leftJoin(customerJpaEntity).on(orderJpaEntity.customerUuid.eq(customerJpaEntity.customerUuid))
                .where(
                        orderCodeCon(condition.orderCode),
                        nameCon(condition.name),
                        orderStateEq(condition.getOrderStatus()),
                        dateBetween(condition.getStartDate(), condition.getEndDate())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }


    private BooleanExpression orderCodeCon(String orderCode) {
        if (!StringUtils.hasText(orderCode)) {
            return null;
        }

        return orderJpaEntity.orderCode.contains(orderCode);
    }


    private BooleanExpression nameCon(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        return customerJpaEntity.firstName.contains(name)
                .or(customerJpaEntity.lastName.contains(name));
    }


    private BooleanExpression orderStateEq(OrderStatus status) {
        if (status == null) {
            return null;
        }
        return orderJpaEntity.orderStatus.eq(status);
    }

    private BooleanExpression dateBetween(LocalDate start, LocalDate end) {

        if (start != null && end != null) {
            return orderJpaEntity.createdAt.between(start.atStartOfDay(), end.atTime(LocalTime.MAX));
        }

        if (start != null) {
            return orderJpaEntity.createdAt.goe(start.atStartOfDay());
        }

        if (end != null) {
            return orderJpaEntity.createdAt.loe(end.atTime(LocalTime.MAX));
        }
        return null;
    }
}
