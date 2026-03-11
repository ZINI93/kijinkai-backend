package com.kijinkai.domain.customer.adapter.out.persistence.repository;

import com.kijinkai.domain.customer.adapter.out.persistence.entity.CustomerJpaEntity;
import com.kijinkai.domain.customer.domain.model.CustomerTier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

import static com.kijinkai.domain.customer.adapter.out.persistence.entity.QCustomerJpaEntity.*;
import static com.kijinkai.domain.user.adapter.out.persistence.entity.QUserJpaEntity.*;


@RequiredArgsConstructor
public class CustomerRepositoryCustomImpl implements CustomerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CustomerJpaEntity> findAllByCustomers(String firstName, String lastName, String phoneNumber, CustomerTier customerTier, Pageable pageable) {

        BooleanExpression predicate = buildPredicate(firstName, lastName, phoneNumber, customerTier);

        JPAQuery<CustomerJpaEntity> query = queryFactory
                .select(customerJpaEntity)
                .where(predicate);


        List<CustomerJpaEntity> content = query
                .orderBy(customerJpaEntity.customerId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, query::fetchCount);
    }


    private BooleanExpression buildPredicate(String firstName, String lastName, String phoneNumber, CustomerTier customerTier) {

        BooleanExpression predicate = Expressions.TRUE;


        BooleanExpression firstNameCondition = filterByFirstName(firstName);
        if (firstNameCondition != null) {
            predicate = predicate.and(firstNameCondition);
        }

        BooleanExpression lastNameCondition = filterByLastName(lastName);
        if (lastNameCondition != null) {
            predicate = predicate.and(lastNameCondition);
        }

        BooleanExpression phoneNumberCondition = filterByPhoneNumber(phoneNumber);
        if (phoneNumberCondition != null) {
            predicate = predicate.and(phoneNumberCondition);
        }

        BooleanExpression customerTierCondition = filterByCustomerTier(customerTier);
        if (customerTierCondition != null) {
            predicate = predicate.and(customerTierCondition);
        }
        return predicate;
    }

    private BooleanExpression filterByUser(UUID customerUuid) {
        if (customerUuid == null) {
            return null;
        }
        return customerJpaEntity.customerUuid.eq(customerUuid);
    }

    private BooleanExpression filterByFirstName(String firstName) {
        if (firstName == null || firstName.isEmpty()) {
            return null;
        }
        return customerJpaEntity.firstName.containsIgnoreCase(firstName);
    }

    private BooleanExpression filterByCustomerTier(CustomerTier customerTier) {
        if (customerTier == null) {
            return null;
        }
        return customerJpaEntity.customerTier.eq(customerTier);
    }

    private BooleanExpression filterByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return null;
        }
        return customerJpaEntity.phoneNumber.containsIgnoreCase(phoneNumber);

    }

    private BooleanExpression filterByLastName(String lastName) {
        if (lastName == null || lastName.isEmpty()) {
            return null;
        }
        return customerJpaEntity.lastName.containsIgnoreCase(lastName);
    }


    // ----

    @Override
    public Page<CustomerJpaEntity> searchCustomers(CustomerSearchCondition condition, Pageable pageable) {

        // 1. 컨텐츠 조회

        List<CustomerJpaEntity> content = queryFactory
                .selectFrom(customerJpaEntity)
                .leftJoin(userJpaEntity).on(customerJpaEntity.userUuid.eq(userJpaEntity.userUuid))
                .where(
                        emailCon(condition.getEmail()),
                        nameCon(condition.getName()),
                        phoneNumberCon(condition.getPhoneNumber()),
                        customerTier(condition.getCustomerTier())

                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(customerJpaEntity.createdAt.desc())
                .fetch();


        JPAQuery<Long> countQuery = queryFactory
                .select(customerJpaEntity.count())
                .from(customerJpaEntity)
                .where(
                        emailCon(condition.getEmail()),
                        nameCon(condition.getName()),
                        phoneNumberCon(condition.getPhoneNumber()),
                        customerTier(condition.getCustomerTier())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


    private BooleanExpression emailCon(String email) {
        if (!StringUtils.hasText(email)) {
            return null;
        }

        return userJpaEntity.email.contains(email);
    }

    private BooleanExpression nameCon(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        StringExpression fullName = customerJpaEntity.firstName.concat(" ").concat(customerJpaEntity.lastName);

        return fullName.contains(name);
    }

    private BooleanExpression phoneNumberCon(String phoneNumber) {
        if (!StringUtils.hasText(phoneNumber)) {
            return null;
        }

        return customerJpaEntity.phoneNumber.contains(phoneNumber);
    }

    private BooleanExpression customerTier(CustomerTier tier) {
        if (tier == null) {
            return null;
        }

        return customerJpaEntity.customerTier.eq(tier);
    }

}


