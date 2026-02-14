package com.kijinkai.domain.post.factory;


import com.kijinkai.domain.post.dto.request.PostRequestDto;
import com.kijinkai.domain.post.entity.PostCategory;
import com.kijinkai.domain.post.entity.PostJpaEntity;
import com.kijinkai.domain.post.entity.PostStatus;
import org.hibernate.annotations.Comment;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class PostEntityFactory {

    public PostJpaEntity createNoticePost(UUID userUuid, PostRequestDto.NoticeCreate request){

        Objects.requireNonNull(request, "request는 null일 수 없습니다");
        Objects.requireNonNull(request.getBaseInfo(), "baseInfo는 필수입니다");


        return PostJpaEntity.builder()
                .postUuid(UUID.randomUUID())
                .authorUuid(userUuid)
                .postCategory(PostCategory.NOTICE)
                .postStatus(PostStatus.PUBLISHED)
                .title(request.getBaseInfo().getTitle())
                .content(request.getBaseInfo().getContent())
                .pinned(request.isPinned())
                .secret(request.isSecret())
                .build();
    }

    public PostJpaEntity createOrderGuide(UUID userUuid, PostRequestDto.BaseInfo request){

        Objects.requireNonNull(request, "request는 null일 수 없습니다");


        return PostJpaEntity.builder()
                .postUuid(UUID.randomUUID())
                .authorUuid(userUuid)
                .postCategory(PostCategory.NOTICE)
                .postStatus(PostStatus.PUBLISHED)
                .title(request.getTitle())
                .content(request.getContent())
                .build();
    }





    public PostJpaEntity createReviewPost(UUID userUuid, PostRequestDto.ReviewCreate request){

        Objects.requireNonNull(request, "request는 null일 수 없습니다");
        Objects.requireNonNull(request.getBaseInfo(), "baseInfo는 필수입니다");

        return PostJpaEntity.builder()
                .postUuid(UUID.randomUUID())
                .authorUuid(userUuid)
                .postCategory(PostCategory.REVIEW)
                .postStatus(PostStatus.PUBLISHED)
                .title(request.getBaseInfo().getTitle())
                .content(request.getBaseInfo().getContent())
                .orderCode(request.getOrderCode())
                .build();
    }



}
