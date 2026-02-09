package com.kijinkai.domain.campaign.adapter.out.repository.campaign;


import com.kijinkai.domain.campaign.adapter.out.entity.CampaignJpaEntity;
import com.kijinkai.domain.campaign.domain.modal.CampaignStatus;
import com.kijinkai.domain.campaign.domain.modal.CampaignType;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.kijinkai.domain.campaign.adapter.out.entity.QCampaignJpaEntity.*;

@RequiredArgsConstructor
public class CampaignJpaEntityRepositoryImpl implements CampaignJpaEntityRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CampaignJpaEntity> searchCampaign(CampaignSearchCondition condition, Pageable pageable) {

        // 1. 컨텐츠 조회
        List<CampaignJpaEntity> content = queryFactory
                .selectFrom(campaignJpaEntity)
                .where(
                        titleCon(condition.getTitle()),
                        campaignTypeEq(condition.getType()),
                        campaignStatusEq(condition.getStatus()),
                        featuredEq(condition.getFeatured()),
                        minParticipants(condition.getMinParticipants()),
                        betweenDate(condition.getSearchStartDate(), condition.getSearchEndDate())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .fetch();


        // 2. 카운트 조회
        JPAQuery<Long> countQuery = queryFactory
                .select(campaignJpaEntity.count())
                .from(campaignJpaEntity)
                .where(
                        titleCon(condition.getTitle()),
                        campaignTypeEq(condition.getType()),
                        campaignStatusEq(condition.getStatus()),
                        featuredEq(condition.getFeatured()),
                        minParticipants(condition.getMinParticipants()),
                        betweenDate(condition.getSearchStartDate(), condition.getSearchEndDate())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }


    //helper method
    private BooleanExpression titleCon(String title) {
        return title != null ? campaignJpaEntity.title.contains(title) : null;
    }

    private BooleanExpression campaignTypeEq(CampaignType type) {
        return type != null ? campaignJpaEntity.campaignType.eq(type) : null;
    }

    private BooleanExpression campaignStatusEq(CampaignStatus status) {
        return status != null ? campaignJpaEntity.campaignStatus.eq(status) : null;
    }

    private BooleanExpression featuredEq(Boolean featured) {
        return featured != null ? campaignJpaEntity.featured.eq(featured) : null;
    }

    private BooleanExpression minParticipants(Integer minParticipants) {
        return minParticipants != null ? campaignJpaEntity.participantCount.goe(minParticipants) : null;
    }


    private BooleanExpression betweenDate(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return null;
        }

        if (endDate == null) return campaignJpaEntity.createdAt.goe(startDate.atStartOfDay());
        if (startDate == null) return campaignJpaEntity.createdAt.loe(endDate.atTime(LocalTime.MAX));


        return campaignJpaEntity.createdAt.between(
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX)
        );
    }


    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        sort.stream().forEach(order -> {

            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String property = order.getProperty();

            switch (property) {
                case "createdAt":
                    orders.add(new OrderSpecifier<>(direction, campaignJpaEntity.createdAt));
                    break;

                case "displayOrder":
                    orders.add(new OrderSpecifier<>(direction, campaignJpaEntity.displayOrder));
                    break;

                case "startDate":
                    orders.add(new OrderSpecifier<>(direction, campaignJpaEntity.startDate));
                    break;

            }

        });

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier<>(Order.DESC, campaignJpaEntity.createdAt));
        }
        return orders.toArray(OrderSpecifier[]::new);
    }


}
