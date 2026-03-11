package com.kijinkai.domain.payment.adapter.out.persistence.repository.deposit;

import com.kijinkai.domain.payment.application.dto.DepositAdminSearchDto;
import com.kijinkai.domain.payment.application.dto.DepositAdminSummaryDto;
import com.kijinkai.domain.payment.application.dto.QDepositAdminSearchDto;
import com.kijinkai.domain.payment.domain.enums.DepositMethod;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.kijinkai.domain.customer.adapter.out.persistence.entity.QCustomerJpaEntity.*;
import static com.kijinkai.domain.payment.adapter.out.persistence.entity.QDepositRequestJpaEntity.*;
import static com.kijinkai.domain.user.adapter.out.persistence.entity.QUserJpaEntity.*;

@RequiredArgsConstructor
public class SpringDataJpaDepositRequestRepositoryImpl implements SpringDataJpaDepositRequestRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public DepositAdminSummaryDto summary(LocalDate date) {

        LocalDateTime startOfMonth = date.withDayOfMonth(1).atStartOfDay();
        LocalDateTime startOfNextMonth = startOfMonth.plusMonths(1);

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return queryFactory
                .select(Projections.constructor(DepositAdminSummaryDto.class,

                        // 총 충전금액
                        new CaseBuilder()
                                .when(depositRequestJpaEntity.status.eq(DepositStatus.APPROVED))
                                .then(depositRequestJpaEntity.amountOriginal)
                                .otherwise(BigDecimal.ZERO)
                                .sum().coalesce(BigDecimal.ZERO),

                        //오늘 충전금액
                        new CaseBuilder()
                                .when(depositRequestJpaEntity.status.eq(DepositStatus.APPROVED)
                                        .and(depositRequestJpaEntity.createdAt.between(startOfDay, endOfDay)))
                                .then(depositRequestJpaEntity.amountOriginal)
                                .otherwise(BigDecimal.ZERO)
                                .sum().coalesce(BigDecimal.ZERO),
                        //입금대기
                        new CaseBuilder()
                                .when(depositRequestJpaEntity.status.eq(DepositStatus.PENDING_ADMIN_APPROVAL))
                                .then(depositRequestJpaEntity.amountOriginal)
                                .otherwise(BigDecimal.ZERO)
                                .sum().coalesce(BigDecimal.ZERO),

                        // 원별 건수
                        depositRequestJpaEntity.count()

                ))
                .from(depositRequestJpaEntity)
                .where(depositRequestJpaEntity.createdAt.goe(startOfMonth),
                        depositRequestJpaEntity.createdAt.lt(startOfNextMonth))
                .fetchOne();

    }

    @Override
    public Page<DepositAdminSearchDto> searchDeposit(DepositSearchCondition condition, Pageable pageable) {

        List<DepositAdminSearchDto> content = queryFactory
                .select(new QDepositAdminSearchDto(
                        depositRequestJpaEntity.requestUuid,
                        depositRequestJpaEntity.customerUuid,
                        depositRequestJpaEntity.depositCode,
                        customerJpaEntity.lastName.concat(customerJpaEntity.firstName),
                        userJpaEntity.email,
                        depositRequestJpaEntity.amountOriginal,
                        depositRequestJpaEntity.depositMethod,
                        depositRequestJpaEntity.depositorName,
                        depositRequestJpaEntity.bankType,
                        depositRequestJpaEntity.status,
                        depositRequestJpaEntity.createdAt,
                        depositRequestJpaEntity.updatedAt
                ))
                .from(depositRequestJpaEntity)
                .leftJoin(customerJpaEntity).on(depositRequestJpaEntity.customerUuid.eq(customerJpaEntity.customerUuid))
                .leftJoin(userJpaEntity).on(customerJpaEntity.userUuid.eq(userJpaEntity.userUuid))
                .where(
                        depositCodeCon(condition.getDepositCode()),
                        nameCon(condition.getName()),
                        depositMethodEq(condition.getDepositMethod()),
                        depositStatus(condition.getDepositStatus()),
                        dateBetween(condition.getStartDate(), condition.getEndDate())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(depositRequestJpaEntity.createdAt.desc())
                .fetch();


        JPAQuery<Long> countQuery = queryFactory
                .select(depositRequestJpaEntity.count())
                .from(depositRequestJpaEntity);


        if (StringUtils.hasText(condition.getName())) {
            countQuery.leftJoin(customerJpaEntity)
                    .on(depositRequestJpaEntity.customerUuid.eq(customerJpaEntity.customerUuid));
        }

        countQuery.where(
                depositCodeCon(condition.getDepositCode()),
                nameCon(condition.getName()),
                depositMethodEq(condition.getDepositMethod()),
                depositStatus(condition.getDepositStatus()),
                dateBetween(condition.getStartDate(), condition.getEndDate())

        );


        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


    private BooleanExpression depositCodeCon(String depositCode) {
        if (!StringUtils.hasText(depositCode)) {
            return null;
        }

        return depositRequestJpaEntity.depositCode.startsWith(depositCode);
    }


    private BooleanExpression nameCon(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }

        return customerJpaEntity.firstName.contains(name)
                .or(customerJpaEntity.lastName.contains(name));
    }

    private BooleanExpression depositMethodEq(DepositMethod depositMethod) {
        if (depositMethod == null) {
            return null;
        }

        return depositRequestJpaEntity.depositMethod.eq(depositMethod);
    }

    private BooleanExpression depositStatus(DepositStatus depositStatus) {
        if (depositStatus == null) {
            return null;
        }

        return depositRequestJpaEntity.status.eq(depositStatus);
    }

    private BooleanExpression dateBetween(LocalDate start, LocalDate end) {


        // 둘 다 없는 경우만 null 반환
        if (start == null && end == null) {
            return null;
        }

        // 시작일만 검색하는 경우
        if (start != null && end == null) {
            return depositRequestJpaEntity.createdAt.goe(start.atStartOfDay());
        }

        // 종료일로 검색하는 경우
        if (start == null && end != null) {
            return depositRequestJpaEntity.createdAt.loe(end.atTime(LocalTime.MAX));
        }

        return depositRequestJpaEntity.createdAt.between(
                start.atStartOfDay(),
                end.atTime(LocalTime.MAX)
        );

    }
}
