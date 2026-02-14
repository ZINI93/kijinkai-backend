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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "Post API", description = "게시글(공지사항) 관리 API")
@RestController
@RequestMapping("/api/v1/posts/notices")
@RequiredArgsConstructor
public class PostNoticeController {

    private final PostFacadeUseCase postFacadeUseCase;


    @Operation(summary = "공지사항 생성", description = "관리자 권한으로 공지사항을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "공지사항 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BasicResponseDto<PostResponseDto>> createNotice(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PostRequestDto.NoticeCreate requestDto) {
        PostResponseDto response = postFacadeUseCase.createNotice(userDetails.getUserUuid(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BasicResponseDto.success("공지사항이 성공적으로 생성되었습니다.", response));
    }


    @Operation(summary = "공지사항 조회", description = "공지사항 상세 정보를 조회합니다.")
    @GetMapping("/{postUuid}")
    public ResponseEntity<BasicResponseDto<PostResponseDto>> getPost(@PathVariable UUID postUuid) {

        PostResponseDto response = postFacadeUseCase.getPost(postUuid);

        return ResponseEntity.ok(BasicResponseDto.success("리뷰 조회 성공", response));
    }


    @Operation(summary = "공지사항 리스트 검색", description = "제목 및 내용으로 게시글을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<BasicResponseDto<Page<PostResponseDto>>> getSearchPosts(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            Pageable pageable) {

        Page<PostResponseDto> response = postFacadeUseCase.getSearchPosts(title, content, PostCategory.NOTICE, pageable);

        return ResponseEntity.ok(BasicResponseDto.success("검색 결과입니다.", response));
    }


    @Operation(summary = "공지사항 수정", description = "관리자 권한으로 공지사항을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공지사항 수정 성공"),
            @ApiResponse(responseCode = "404", description = "공지사항을 찾을 수 없음"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PutMapping(value = "/{postUuid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BasicResponseDto<PostResponseDto>> updateNotice(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID postUuid,
            @RequestBody PostUpdateDto.NoticeUpdate updateDto) {
        PostResponseDto response = postFacadeUseCase.updateNotice(userDetails.getUserUuid(), postUuid, updateDto);
        return ResponseEntity.ok(BasicResponseDto.success("공지사항이 수정되었습니다.", response));
    }

    @Operation(summary = "공지사항 삭제", description = "관리자 권한으로 공지사항을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공지사항 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "공지사항을 찾을 수 없음")
    })
    @DeleteMapping("/{postUuid}")
    public ResponseEntity<BasicResponseDto<Void>> deleteNotice(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID postUuid) {
        postFacadeUseCase.deleteNotice(userDetails.getUserUuid(), postUuid);
        return ResponseEntity.ok(BasicResponseDto.success("공지사항이 삭제되었습니다.", null));
    }





    @Operation(summary = "관리자 게시글 통합 검색", description = "관리자용 필터를 사용하여 게시글을 검색합니다.")
    @GetMapping("/admin/search")
    public ResponseEntity<BasicResponseDto<Page<PostResponseDto>>> getSearchPostsByAdmin(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) PostCategory postCategory,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {
        Page<PostResponseDto> response = postFacadeUseCase.getSearchPostsByAdmin(
                userDetails.getUserUuid(), title, content, nickname, postCategory, startDate, endDate, pageable);
        return ResponseEntity.ok(BasicResponseDto.success("게시글 검색 결과입니다.", response));
    }
}
