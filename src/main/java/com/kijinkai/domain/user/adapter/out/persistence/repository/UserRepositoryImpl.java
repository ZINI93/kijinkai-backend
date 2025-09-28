package com.kijinkai.domain.user.adapter.out.persistence.repository;

import com.kijinkai.domain.user.adapter.out.persistence.entity.QUserJpaEntity;
import com.kijinkai.domain.user.adapter.out.persistence.entity.UserJpaEntity;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;


@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<UserJpaEntity> findAllByEmailAndNickName(String email, String nickname, Pageable pageable) {


        JPAQuery<UserJpaEntity> query = queryFactory
                .select(QUserJpaEntity.userJpaEntity)
                .where(
                        emailCond(email),
                        nicknameCond(nickname)
                );


        List<UserJpaEntity> content = query
                .orderBy(QUserJpaEntity.userJpaEntity.userId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, query::fetchCount);
    }

    private BooleanExpression emailCond(String emailCnd) {
        return containCond(emailCnd, QUserJpaEntity.userJpaEntity.email);
    }

    private Predicate nicknameCond(String nicknameCnd) {
        return containCond(nicknameCnd, QUserJpaEntity.userJpaEntity.nickname);
    }

    private static BooleanExpression containCond(String value, StringPath field){
        if (value == null || value.isEmpty()) {
            return null;
        }
        return field.containsIgnoreCase(value);
    }
}
