package com.kijinkai.domain.post.controller;

import com.kijinkai.domain.common.BasicResponseDto;
import com.kijinkai.domain.post.dto.request.PostRequestDto;
import com.kijinkai.domain.post.dto.request.PostUpdateDto;
import com.kijinkai.domain.post.dto.response.PostResponseDto;
import com.kijinkai.domain.post.entity.PostCategory;
import com.kijinkai.domain.post.service.facade.PostFacadeUseCase;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.UUID;

@Tag(name = "Post API", description = "게시글(공지사항 및 리뷰) 관리 API")
@RestController
@RequestMapping("/api/v1/posts/reviews")
@RequiredArgsConstructor
public class PostReviewController {

    private final PostFacadeUseCase postFacadeUseCase;

    @Operation(summary = "리뷰 작성", description = "이미지를 포함하여 리뷰를 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "리뷰 생성 성공")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BasicResponseDto<PostResponseDto>> createReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("requestDto") PostRequestDto.ReviewCreate requestDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        PostResponseDto response = postFacadeUseCase.createReview(userDetails.getUserUuid(), requestDto, image);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BasicResponseDto.success("리뷰가 등록되었습니다.", response));
    }

    @Operation(summary = "리뷰 수정", description = "리뷰 내용 및 이미지를 수정합니다.")
    @PutMapping(value = "/{postUuid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BasicResponseDto<PostResponseDto>> updateReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID postUuid,
            @RequestPart("updateDto") PostUpdateDto.ReviewUpdate updateDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        PostResponseDto response = postFacadeUseCase.updateReview(userDetails.getUserUuid(), postUuid, updateDto, image);

        return ResponseEntity.ok(BasicResponseDto.success("리뷰가 수정되었습니다.", response));
    }

    @Operation(summary = "리뷰 상세 조회", description = "특정 리뷰의 상세 정보를 조회합니다.")
    @GetMapping("/reviews/{postUuid}")
    public ResponseEntity<BasicResponseDto<PostResponseDto>> getReview(@PathVariable UUID postUuid) {

        PostResponseDto response = postFacadeUseCase.getReview(postUuid);

        return ResponseEntity.ok(BasicResponseDto.success("리뷰 조회 성공", response));
    }

    @Operation(summary = "리뷰 삭제", description = "작성자가 본인의 리뷰를 삭제합니다.")
    @DeleteMapping("/reviews/{postUuid}")
    public ResponseEntity<BasicResponseDto<Void>> deleteReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID postUuid) {

        postFacadeUseCase.deleteReview(userDetails.getUserUuid(), postUuid);

        return ResponseEntity.ok(BasicResponseDto.success("리뷰가 삭제되었습니다.", null));
    }

    @Operation(summary = "리뷰 검색", description = "제목 및 내용으로 게시글을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<BasicResponseDto<Page<PostResponseDto>>> getSearchPosts(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            Pageable pageable) {

        UUID currentUserUuid = (customUserDetails != null) ? customUserDetails.getUserUuid() : null;

        Page<PostResponseDto> response = postFacadeUseCase.getSearchReview(currentUserUuid, title, content, PostCategory.REVIEW, pageable);

        return ResponseEntity.ok(BasicResponseDto.success("검색 결과입니다.", response));
    }
}
