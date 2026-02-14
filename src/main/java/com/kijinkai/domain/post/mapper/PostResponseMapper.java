package com.kijinkai.domain.post.mapper;


import com.kijinkai.domain.post.dto.response.PostResponseDto;
import com.kijinkai.domain.post.entity.PostImageJpaEntity;
import com.kijinkai.domain.post.entity.PostJpaEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PostResponseMapper {

    public PostResponseDto toNoticeDetailResponse(PostJpaEntity post) {

        return PostResponseDto.builder()
                .postUuid(post.getPostUuid())
                .build();
    }

    public PostResponseDto toCreateResponse(PostJpaEntity post, PostImageJpaEntity postImage) {

        return PostResponseDto.builder()
                .postUuid(post.getPostUuid())
                .postImageUuid(postImage.getPostImageUuid())
                .build();
    }

    /*
    공지사항 리스트
     */
    public PostResponseDto toResponse(PostJpaEntity post) {
        return PostResponseDto.builder()
                .postUuid(post.getPostUuid())
                .build();
    }


    /*
    리뷰 리스트
     */
    public PostResponseDto toReviewResponse(PostJpaEntity post, String nickname, PostImageJpaEntity postImage, UUID cureentUserUuid) {

        String maskedNickname = nickname;
        if (nickname != null && !nickname.isEmpty()){
            maskedNickname = nickname.charAt(0) + "**";
        }
        return PostResponseDto.builder()
                .nickname(maskedNickname)
                .postUuid(post.getPostUuid())
                .isOwner(post.getAuthorUuid().equals(cureentUserUuid))
                .imageUrl(postImage.getImageUrl())
                .title(post.getTitle())
                .content(post.getContent())
                .createAt(post.getCreatedAt().toLocalDate())
                .build();
    }


    /*
   공지사항 리스트
    */
    public PostResponseDto toNoticeListResponse(PostJpaEntity post) {
        return PostResponseDto.builder()
                .postUuid(post.getPostUuid())
                .title(post.getTitle())
                .content(post.getContent())
                .createAt(post.getCreatedAt().toLocalDate())
                .viewCount(post.getViewCount())
                .pinned(post.isPinned())
                .build();
    }


    public PostResponseDto toReviewDetailResponse(PostJpaEntity post, PostImageJpaEntity postImage) {

        return PostResponseDto.builder()
                .postUuid(post.getPostUuid())
                .build();
    }


    public PostResponseDto toPostDetailResponse(PostJpaEntity post) {

        return PostResponseDto.builder()
                .postUuid(post.getPostUuid())
                .title(post.getTitle())
                .content(post.getContent())
                .createAt(post.getCreatedAt().toLocalDate())
                .viewCount(post.getViewCount())
                .build();
    }

}
