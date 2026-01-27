package com.kijinkai.domain.transaction.repository;


import com.kijinkai.domain.transaction.entity.Transaction;
import com.kijinkai.domain.transaction.entity.TransactionType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

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

        return new PageImpl<>(content,pageable, total != null ? total : 0);


    }


    // -- helper method
    private BooleanExpression customerUuidEq(UUID customerUuid) {
        return customerUuid != null ? transaction.customerUuid.eq(customerUuid) : null;
    }

    private BooleanExpression transactionTypeEq(TransactionType type) {
        return type != null ? transaction.transactionType.eq(type) : null;
    }

    private BooleanExpression dateBetween(LocalDate start, LocalDate end) {

        // 둘다 없는경우
        if (start == null || end == null) {
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
