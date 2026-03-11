package com.kijinkai.domain.transaction.repository;


import com.kijinkai.domain.transaction.dto.TransactionAdminSummaryDto;
import com.kijinkai.domain.transaction.entity.Transaction;
import com.kijinkai.domain.transaction.entity.TransactionStatus;
import com.kijinkai.domain.transaction.entity.TransactionType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static com.kijinkai.domain.customer.adapter.out.persistence.entity.QCustomerJpaEntity.*;
import static com.kijinkai.domain.transaction.entity.QTransaction.transaction;


@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Transaction> search(TransactionSearchCondition condition, Pageable pageable) {


        // 1. 컨텐츠 조회
        List<Transaction> content = queryFactory.
                selectFrom(transaction)
                .where(
                        customerUuidEq(condition.getCustomerUuid()),
                        transactionTypeEq(condition.getType()),
                        dateBetween(condition.getStarDate(), condition.getEndDate())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(transaction.createdAt.desc())
                .fetch();


        // 2.카운트 쿼리
        Long total = queryFactory
                .select(transaction.count())
                .from(transaction)
                .where(
                        customerUuidEq(condition.getCustomerUuid()),
                        transactionTypeEq(condition.getType()),
                        dateBetween(condition.getStarDate(), condition.getEndDate())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);


    }

    @Override
    public Page<Transaction> searchByAdmin(TransactionSearchCondition condition, Pageable pageable) {

        List<Transaction> content = queryFactory
                .selectFrom(transaction)
                .leftJoin(customerJpaEntity).on(transaction.customerUuid.eq(customerJpaEntity.customerUuid))
                .where(
                        nameCon(condition.getName()),
                        phoneNoCon(condition.getPhoneNumber()),
                        paymentCodeCon(condition.getPaymentCode()),
                        transactionStatusEq(condition.getTransactionStatus()),
                        transactionTypeEq(condition.getType()),
                        dateBetween(condition.getStarDate(), condition.getEndDate())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(transaction.createdAt.desc())
                .fetch();


        JPAQuery<Long> countQuery = queryFactory
                .select(transaction.count())
                .from(transaction)
                .leftJoin(customerJpaEntity).on(transaction.customerUuid.eq(customerJpaEntity.customerUuid))
                .where(
                        nameCon(condition.getName()),
                        phoneNoCon(condition.getPhoneNumber()),
                        paymentCodeCon(condition.getPaymentCode()),
                        transactionStatusEq(condition.getTransactionStatus()),
                        transactionTypeEq(condition.getType()),
                        dateBetween(condition.getStarDate(), condition.getEndDate())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


    @Override
    public TransactionAdminSummaryDto getTransactionSummary(LocalDate date) {

        // 해당 월의 시작일
        LocalDateTime startOfMonth = date.withDayOfMonth(1).atStartOfDay();
        // 다음 달의 시작일
        LocalDateTime startOfNextMonth = startOfMonth.plusMonths(1);

        return queryFactory
                .select(Projections.constructor(TransactionAdminSummaryDto.class,

                        // 완료된 건수
                        new CaseBuilder()
                                .when(transaction.transactionStatus.eq(TransactionStatus.COMPLETED))
                                .then(transaction.amount)
                                .otherwise(BigDecimal.ZERO)
                                .sum(),
                        new CaseBuilder()
                                .when(transaction.transactionStatus.eq(TransactionStatus.REQUEST))
                                .then(transaction.amount)
                                .otherwise(BigDecimal.ZERO)
                                .sum(),
                        new CaseBuilder()
                                .when(transaction.transactionStatus.eq(TransactionStatus.REFUND))
                                .then(transaction.amount)
                                .otherwise(BigDecimal.ZERO)
                                .sum(),
                        transaction.count()
                ))
                .from(transaction)
                .where(transaction.createdAt.goe(startOfMonth),
                        transaction.createdAt.lt(startOfNextMonth)
                )
                .fetchOne();
    }


    // -- helper method
    private BooleanExpression nameCon(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        return customerJpaEntity.firstName.contains(name)
                .or(customerJpaEntity.lastName.contains(name));
    }

    private BooleanExpression phoneNoCon(String phoneNo) {
        if (!StringUtils.hasText(phoneNo)) {
            return null;
        }

        return customerJpaEntity.phoneNumber.contains(phoneNo);
    }

    private BooleanExpression paymentCodeCon(String paymentCode) {
        if (!StringUtils.hasText(paymentCode)) {
            return null;
        }

        return transaction.paymentCode.contains(paymentCode);
    }

    private BooleanExpression transactionStatusEq(TransactionStatus transactionStatus) {
        if (transactionStatus == null) {
            return null;
        }
        return transaction.transactionStatus.eq(transactionStatus);
    }

    private BooleanExpression customerUuidEq(UUID customerUuid) {
        return customerUuid != null ? transaction.customerUuid.eq(customerUuid) : null;
    }

    private BooleanExpression transactionTypeEq(TransactionType type) {
        return type != null ? transaction.transactionType.eq(type) : null;
    }

    private BooleanExpression dateBetween(LocalDate start, LocalDate end) {

        // 둘다 없는경우
        if (start == null && end == null) {
            return null;
        }

        // 2. 시작일만 있는 경우 (해당 날짜 00시부터 미래 전체)
        if (start != null && end == null) {
            return transaction.createdAt.goe(start.atStartOfDay());
        }

        // 3. 종료일만 있는 경우 (과거 전체부터 해당 날짜 23시 59분까지)
        if (start == null && end != null) {
            return transaction.createdAt.loe(end.atTime(LocalTime.MAX));
        }

        return transaction.createdAt.between(
                start.atStartOfDay(),
                end.atTime(LocalTime.MAX)
        );

    }
}
