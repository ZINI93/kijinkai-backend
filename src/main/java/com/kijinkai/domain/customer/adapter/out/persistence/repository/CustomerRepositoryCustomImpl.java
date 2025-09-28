package com.kijinkai.domain.customer.adapter.out.persistence.repository;

import com.kijinkai.domain.customer.adapter.out.persistence.entity.CustomerJpaEntity;
import com.kijinkai.domain.customer.adapter.out.persistence.entity.QCustomerJpaEntity;
import com.kijinkai.domain.customer.domain.model.CustomerTier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.UUID;



@RequiredArgsConstructor
public class CustomerRepositoryCustomImpl implements CustomerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CustomerJpaEntity> findAllByCustomers(String firstName, String lastName, String phoneNumber, CustomerTier customerTier, Pageable pageable) {

        BooleanExpression predicate = buildPredicate(firstName, lastName, phoneNumber, customerTier);

        JPAQuery<CustomerJpaEntity> query = queryFactory
                .select(QCustomerJpaEntity.customerJpaEntity)
                .where(predicate);


        List<CustomerJpaEntity> content = query
                .orderBy(QCustomerJpaEntity.customerJpaEntity.customerId.desc())
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
        return QCustomerJpaEntity.customerJpaEntity.customerUuid.eq(customerUuid);
    }

    private BooleanExpression filterByFirstName(String firstName) {
        if (firstName == null || firstName.isEmpty()) {
            return null;
        }
        return QCustomerJpaEntity.customerJpaEntity.firstName.containsIgnoreCase(firstName);
    }

    private BooleanExpression filterByCustomerTier(CustomerTier customerTier) {
        if (customerTier == null) {
            return null;
        }
        return QCustomerJpaEntity.customerJpaEntity.customerTier.eq(customerTier);
    }

    private BooleanExpression filterByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return null;
        }
        return QCustomerJpaEntity.customerJpaEntity.phoneNumber.containsIgnoreCase(phoneNumber);

    }

    private BooleanExpression filterByLastName(String lastName) {
        if (lastName == null || lastName.isEmpty()) {
            return null;
        }
        return QCustomerJpaEntity.customerJpaEntity.lastName.containsIgnoreCase(lastName);
    }
}


