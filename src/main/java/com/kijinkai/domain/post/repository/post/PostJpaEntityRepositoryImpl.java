package com.kijinkai.domain.post.repository.post;

import com.kijinkai.domain.post.entity.PostCategory;
import com.kijinkai.domain.post.entity.PostJpaEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static com.kijinkai.domain.post.entity.QPostJpaEntity.*;

@RequiredArgsConstructor
public class PostJpaEntityRepositoryImpl implements PostJpaEntityRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<PostJpaEntity> searchPost(PostCondition postCondition, Pageable pageable) {

        // 컨텐츠 조회
        List<PostJpaEntity> content = queryFactory
                .selectFrom(postJpaEntity)
                .where(
                        titleCon(postCondition.getTitle()),
                        contentCon(postCondition.getContent()),
                        authorUuidEq(postCondition.getAuthorUuid()),
                        categoryEq(postCondition.getPostCategory()),
                        betweenDate(postCondition.getStartDate(), postCondition.getEndDate())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(postJpaEntity.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(postJpaEntity.count())
                .from(postJpaEntity)
                .where(
                        titleCon(postCondition.getTitle()),
                        contentCon(postCondition.getContent()),
                        authorUuidEq(postCondition.getAuthorUuid()),
                        categoryEq(postCondition.getPostCategory()),
                        betweenDate(postCondition.getStartDate(), postCondition.getEndDate())
                );
        return PageableExecutionUtils.getPage(content, pageable, () -> {
            Long count = countQuery.fetchOne();
            return count != null ? count : 0L;
        });

    }

    private BooleanExpression titleCon(String title) {
        return title != null ? postJpaEntity.title.contains(title) : null;
    }

    private BooleanExpression contentCon(String content) {
        return content != null ? postJpaEntity.content.contains(content) : null;
    }

    private BooleanExpression authorUuidEq(UUID authorUuid) {
        return authorUuid != null ? postJpaEntity.authorUuid.eq(authorUuid) : null;
    }

    private BooleanExpression categoryEq(PostCategory postCategory) {
        return postCategory != null ? postJpaEntity.postCategory.eq(postCategory) : null;
    }

    private BooleanExpression betweenDate(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return postJpaEntity.createdAt.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
        }
        if (startDate != null) {
            // 시작일만 있으면 그 날짜부터 쭉 (goe)
            return postJpaEntity.createdAt.goe(startDate.atStartOfDay());
        }
        if (endDate != null) {
            // 종료일만 있으면 그 날짜까지 (loe)
            return postJpaEntity.createdAt.loe(endDate.atTime(LocalTime.MAX));
        }
        return null;

    }


}
