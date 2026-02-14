package com.kijinkai.domain.post.service.facade;


import com.kijinkai.domain.post.dto.request.PostRequestDto;
import com.kijinkai.domain.post.dto.request.PostUpdateDto;
import com.kijinkai.domain.post.dto.response.PostResponseDto;
import com.kijinkai.domain.post.entity.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

public interface PostFacadeUseCase {


    //공지사항
    PostResponseDto createNotice(UUID userAdminUuid, PostRequestDto.NoticeCreate requestDto);

    PostResponseDto updateNotice(UUID userAdminUuid, UUID postUuid, PostUpdateDto.NoticeUpdate updateDto);

    PostResponseDto getPost(UUID postUuid);

    void deleteNotice(UUID userAdminUuid, UUID postUuid);

    Page<PostResponseDto> getSearchPostsByAdmin(UUID userAdminUuid, String title, String content, String nickname, PostCategory postCategory, LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<PostResponseDto> getSearchReview(UUID userUuid ,String title, String content, PostCategory postCategory, Pageable pageable);


    //리뷰
    PostResponseDto createReview(UUID userUuid, PostRequestDto.ReviewCreate requestDto, MultipartFile image);

    PostResponseDto updateReview(UUID userUuid, UUID postUuid, PostUpdateDto.ReviewUpdate updateDto, MultipartFile image);

    PostResponseDto getReview(UUID postUuid);

    void  deleteReview(UUID authorUserUuid, UUID postUuid);

    Page<PostResponseDto> getSearchPosts(String title, String content, PostCategory postCategory, Pageable pageable);


}

