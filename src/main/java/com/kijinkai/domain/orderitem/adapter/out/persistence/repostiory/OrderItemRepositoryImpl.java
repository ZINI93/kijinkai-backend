package com.kijinkai.domain.orderitem.adapter.out.persistence.repostiory;

import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemJpaEntity;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpression;
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
                .leftJoin(customerJpaEntity).on(orderItemJpaEntity.customerUuid.eq(customerJpaEntity.customerUuid))
                .where(
                        orderItemCodeCon(condition.getOrderItemCode()),
                        nameCon((condition.getName())),
                        orderItemStatusEq(condition.getStatus()),
                        dateBetween(condition.getStartDate(), condition.getEndDate())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderItemJpaEntity.createdAt.desc())
                .fetch();


        //카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(orderItemJpaEntity.count())
                .from(orderItemJpaEntity)
                .leftJoin(customerJpaEntity).on(orderItemJpaEntity.customerUuid.eq(customerJpaEntity.customerUuid))                .where(
                        orderItemCodeCon(condition.getOrderItemCode()),
                        nameCon((condition.getName())),
                        orderItemStatusEq(condition.getStatus()),
                        dateBetween(condition.getStartDate(), condition.getEndDate())
                );


        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }


    // helper method
    private BooleanExpression orderItemCodeCon(String orderItemCode) {
        if (!StringUtils.hasText(orderItemCode)) {
            return null;
        }

        return orderItemJpaEntity.orderItemCode.contains(orderItemCode);
    }


    private BooleanExpression nameCon(String name) {
      if (!StringUtils.hasText(name)){
          return null;
      }

        BooleanExpression firstNameContains = customerJpaEntity.firstName.contains(name);
        BooleanExpression lastNameContains = customerJpaEntity.lastName.contains(name);
        BooleanExpression fullNameCon = customerJpaEntity.lastName.concat(customerJpaEntity.firstName).contains(name);

        return firstNameContains.or(lastNameContains).or(fullNameCon);
    }


    private BooleanExpression orderItemStatusEq(OrderItemStatus status) {
        return status != null ? orderItemJpaEntity.orderItemStatus.eq(status) : null;
    }

    private BooleanExpression dateBetween(LocalDate start, LocalDate end) {


        // 둘 다 없는 경우만 null 반환
        if (start == null && end == null) {
            return null;
        }

        // 시작일만 검색하는 경우
        if (start != null && end == null) {
            return orderItemJpaEntity.createdAt.goe(start.atStartOfDay());
        }

        // 종료일로 검색하는 경우
        if (start == null && end != null) {
            return orderItemJpaEntity.createdAt.loe(end.atTime(LocalTime.MAX));
        }

        return orderItemJpaEntity.createdAt.between(
                start.atStartOfDay(),
                end.atTime(LocalTime.MAX)
        );


    }


}
