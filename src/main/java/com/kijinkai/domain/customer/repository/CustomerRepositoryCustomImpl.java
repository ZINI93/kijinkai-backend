package com.kijinkai.domain.customer.repository;

import com.kijinkai.domain.customer.dto.CustomerResponseDto;
import com.kijinkai.domain.customer.dto.QCustomerResponseDto;
import com.kijinkai.domain.customer.entity.CustomerTier;
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

import static com.kijinkai.domain.customer.entity.QCustomer.*;


@RequiredArgsConstructor
public class CustomerRepositoryCustomImpl implements CustomerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CustomerResponseDto> findAllByCustomers(UUID userUuid, String firstName, String lastName, String phoneNumber, CustomerTier customerTier, Pageable pageable) {

        BooleanExpression predicate = buildPredicate(userUuid, firstName, lastName, phoneNumber, customerTier);

        JPAQuery<CustomerResponseDto> query = queryFactory
                .select(new QCustomerResponseDto(
                        customer.firstName,
                        customer.lastName,
                        customer.phoneNumber,
                        customer.customerTier
                ))
                .from(customer)
                .where(predicate);


        List<CustomerResponseDto> content = query
                .orderBy(customer.customerId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, query::fetchCount);
    }

    private BooleanExpression buildPredicate(UUID userUuid, String firstName, String lastName, String phoneNumber, CustomerTier customerTier) {

        BooleanExpression predicate = Expressions.TRUE;

        BooleanExpression userCondition = filterByUser(userUuid);
        if (userCondition != null) {
            predicate = predicate.and(userCondition);
        }

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

    private BooleanExpression filterByUser(UUID userUuid) {
        if (userUuid == null) {
            return null;
        }
        return customer.user.userUuid.eq(userUuid);
    }

    private BooleanExpression filterByFirstName(String firstName) {
        if (firstName == null || firstName.isEmpty()) {
            return null;
        }
        return customer.firstName.containsIgnoreCase(firstName);
    }

    private BooleanExpression filterByCustomerTier(CustomerTier customerTier) {
        if (customerTier == null) {
            return null;
        }
        return customer.customerTier.eq(customerTier);
    }

    private BooleanExpression filterByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return null;
        }
        return customer.phoneNumber.containsIgnoreCase(phoneNumber);

    }

    private BooleanExpression filterByLastName(String lastName) {
        if (lastName == null || lastName.isEmpty()) {
            return null;
        }
        return customer.lastName.containsIgnoreCase(lastName);
    }
}


