package com.kijinkai.domain.post.service.post;


import com.kijinkai.domain.post.dto.request.PostRequestDto;
import com.kijinkai.domain.post.dto.request.PostUpdateDto;
import com.kijinkai.domain.post.entity.PostCategory;
import com.kijinkai.domain.post.entity.PostJpaEntity;
import com.kijinkai.domain.post.exception.PostNotFoundException;
import com.kijinkai.domain.post.factory.PostEntityFactory;
import com.kijinkai.domain.post.mapper.PostResponseMapper;
import com.kijinkai.domain.post.repository.post.PostCondition;
import com.kijinkai.domain.post.repository.post.PostJpaEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostApplicationServiceImpl implements PostApplicationService {


    private final PostJpaEntityRepository postJpaEntityRepository;
    private final PostEntityFactory postEntityFactory;
    private final PostResponseMapper postResponseMapper;

    @Override
    @Transactional
    public PostJpaEntity saveNotice(UUID userAdminUuid, PostRequestDto.NoticeCreate requestDto) {

        //생성
        PostJpaEntity noticePost = postEntityFactory.createNoticePost(userAdminUuid, requestDto);

        //저장
        return postJpaEntityRepository.save(noticePost);
    }

    @Override
    @Transactional
    public PostJpaEntity updateNotice(UUID postUuid, PostUpdateDto.NoticeUpdate updateDto) {

        // 조회
        PostJpaEntity post = findPostByPostUuid(postUuid);

        // 변경
        post.updateNotice(updateDto);

        // 저장 명시
        return postJpaEntityRepository.save(post);
    }


    @Override
    @Transactional
    public void deleteNotice(UUID postUuid) {
        PostJpaEntity post = findPostByPostUuid(postUuid);
        postJpaEntityRepository.delete(post);
    }

    @Override
    public Page<PostJpaEntity> getSearchPostsByAdmin(String title, String content, UUID authorUuid, PostCategory postCategory, LocalDate startDate, LocalDate endDate, Pageable pageable) {

        PostCondition post = PostCondition.builder()
                .title(title)
                .content(content)
                .authorUuid(authorUuid)
                .postCategory(postCategory)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        return postJpaEntityRepository.searchPost(post, pageable);
    }

    @Override
    @Transactional
    public PostJpaEntity saveReview(UUID userUuid, PostRequestDto.ReviewCreate requestDto) {

        PostJpaEntity reviewPost = postEntityFactory.createReviewPost(userUuid, requestDto);

        return postJpaEntityRepository.save(reviewPost);

    }

    @Override
    @Transactional
    public PostJpaEntity updateReview(UUID authorUserUuid, UUID postUuid, PostUpdateDto.ReviewUpdate requestDto) {

        // 조회
        PostJpaEntity post = findPostByAuthorUuidAndPostUuid(authorUserUuid, postUuid);

        // 변경
        post.updateReview(requestDto);

        // 저장 -> 명시적
        return postJpaEntityRepository.save(post);
    }



    @Override
    public PostJpaEntity getPost(UUID postUuid) {
        return findPostByPostUuid(postUuid);
    }

    @Override
    public Page<PostJpaEntity> getSearchPosts(String title, String content, PostCategory postCategory, Pageable pageable) {

        PostCondition post = PostCondition.builder()
                .title(title)
                .content(content)
                .postCategory(postCategory)
                .build();

        return postJpaEntityRepository.searchPost(post, pageable);
    }


    @Override
    @Transactional
    public void deleteReview(UUID userUuid, UUID postUuid) {
        PostJpaEntity post = findPostByAuthorUuidAndPostUuid(userUuid, postUuid);
        postJpaEntityRepository.delete(post);

    }




    // helper
    private PostJpaEntity findPostByPostUuid(UUID postUuid) {
        return postJpaEntityRepository.findByPostUuid(postUuid)
                .orElseThrow(() -> new PostNotFoundException("해당 포스트를 찾을 수 없습니다."));
    }


    private PostJpaEntity findPostByAuthorUuidAndPostUuid(UUID authorUserUuid, UUID postUuid) {
        return postJpaEntityRepository.findByAuthorUuidAndPostUuid(authorUserUuid, postUuid)
                .orElseThrow(() -> new PostNotFoundException("해당 포스트를 찾을 수 없습니다."));
    }
}
