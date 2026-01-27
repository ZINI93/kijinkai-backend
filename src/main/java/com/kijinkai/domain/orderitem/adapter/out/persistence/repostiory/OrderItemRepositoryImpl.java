package com.kijinkai.domain.orderitem.adapter.out.persistence.repostiory;

import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemJpaEntity;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.kijinkai.domain.orderitem.adapter.out.persistence.entity.QOrderItemJpaEntity.*;
import static com.kijinkai.domain.transaction.entity.QTransaction.transaction;

@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<OrderItemJpaEntity> searchAdminOrderItemsByStatus(OrderItemSearchCondition condition, Pageable pageable) {

        // 1. 컨텐츠 조회
        List<OrderItemJpaEntity> content = queryFactory
                .selectFrom(orderItemJpaEntity)
                .where(
                        orderItemStatusEq(condition.getStatus()),
                        orderItemCodeEq(condition.getOrderItemCode()),
                        dateBetween(condition.getStartDate(), condition.getEndDate())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderItemJpaEntity.createdAt.desc())
                .fetch();


        //카운트 쿼리
        Long total = queryFactory
                .select(orderItemJpaEntity.count())
                .from(orderItemJpaEntity)
                .where(
                        orderItemStatusEq(condition.getStatus()),
                        orderItemCodeEq(condition.getOrderItemCode()),
                        dateBetween(condition.getStartDate(), condition.getEndDate())
                )
                .fetchOne();


        return new PageImpl<>(content, pageable, total != null ? total : 0);

    }


    // helper method
    private BooleanExpression orderItemStatusEq(OrderItemStatus status) {
        return status != null ? orderItemJpaEntity.orderItemStatus.eq(status) : null;
    }

    private BooleanExpression orderItemCodeEq(String orderItemCode) {
        return orderItemCode != null ? orderItemJpaEntity.orderItemCode.eq(orderItemCode) : null;
    }

    private BooleanExpression dateBetween(LocalDate start, LocalDate end) {


        // 둘다 없는 경우
        if (start == null || end == null) {
            return null;
        }

        // 시작일만 검색하는 경우
        if (start != null && end == null) {
            return orderItemJpaEntity.createdAt.goe(start.atStartOfDay());
        }

        // 종료일로 검색하는 경우
        if (start == null && end != null) {
            return transaction.createdAt.loe(end.atTime(LocalTime.MAX));
        }

        return orderItemJpaEntity.createdAt.between(
                start.atStartOfDay(),
                end.atTime(LocalTime.MAX)
        );


    }


}
