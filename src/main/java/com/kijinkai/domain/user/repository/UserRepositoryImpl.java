package com.kijinkai.domain.user.repository;

import com.kijinkai.domain.user.dto.QUserResponseDto;
import com.kijinkai.domain.user.dto.UserResponseDto;
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

import static com.kijinkai.domain.user.entity.QUser.*;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<UserResponseDto> findByNameAndNickname(String email, String nickname, Pageable pageable) {

        JPAQuery<UserResponseDto> query = queryFactory
                .select(new QUserResponseDto(
                        user.email,
                        user.nickname))
                .from(user)
                .where(emailCond(email),
                        nicknameCond(nickname)
                );

        List<UserResponseDto> content = query
                .orderBy(user.userId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, query::fetchCount);
    }

    private BooleanExpression emailCond(String emailCnd) {
        return containCond(emailCnd, user.email);
    }

    private Predicate nicknameCond(String nicknameCnd) {
        return containCond(nicknameCnd, user.nickname);
    }

    private static BooleanExpression containCond(String value, StringPath field){
        if (value == null || value.isEmpty()) {
            return null;
        }
        return field.containsIgnoreCase(value);
    }
}
