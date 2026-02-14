package com.kijinkai.domain.post.service.post;


import com.kijinkai.domain.post.dto.request.PostRequestDto;
import com.kijinkai.domain.post.dto.request.PostUpdateDto;
import com.kijinkai.domain.post.dto.response.PostResponseDto;
import com.kijinkai.domain.post.entity.PostCategory;
import com.kijinkai.domain.post.entity.PostJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface PostApplicationService {

    // -- 공지사항

    PostJpaEntity saveNotice(UUID userAdminUuid, PostRequestDto.NoticeCreate requestDto);

    PostJpaEntity updateNotice(UUID postUuid, PostUpdateDto.NoticeUpdate updateDto);

    void deleteNotice(UUID postUuid);

    Page<PostJpaEntity> getSearchPostsByAdmin(String title, String content, UUID authorUuid, PostCategory postCategory, LocalDate startDate, LocalDate endDate, Pageable pageable);

    // -- 리뷰

    PostJpaEntity saveReview(UUID userUuid, PostRequestDto.ReviewCreate requestDto);

    PostJpaEntity updateReview(UUID authorUserUuid, UUID postUuid, PostUpdateDto.ReviewUpdate requestDto);



    // 타입별 상세 조회
    PostJpaEntity getPost(UUID postUuid);

    Page<PostJpaEntity> getSearchPosts(String title, String content, PostCategory postCategory, Pageable pageable);

    void deleteReview(UUID userUuid, UUID postUuid);
}
