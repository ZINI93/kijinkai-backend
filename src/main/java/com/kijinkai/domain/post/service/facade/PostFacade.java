package com.kijinkai.domain.post.service.facade;

import com.kijinkai.domain.order.application.port.in.UpdateOrderUseCase;
import com.kijinkai.domain.post.dto.request.PostRequestDto;
import com.kijinkai.domain.post.dto.request.PostUpdateDto;
import com.kijinkai.domain.post.dto.response.PostResponseDto;
import com.kijinkai.domain.post.entity.PostCategory;
import com.kijinkai.domain.post.entity.PostImageJpaEntity;
import com.kijinkai.domain.post.entity.PostJpaEntity;
import com.kijinkai.domain.post.mapper.PostResponseMapper;
import com.kijinkai.domain.post.service.post.PostApplicationService;
import com.kijinkai.domain.post.service.postimage.PostImageApplicationService;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import com.kijinkai.domain.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class PostFacade implements PostFacadeUseCase {

    private final PostApplicationService postApplicationService;
    private final PostResponseMapper postResponseMapper;

    //외부
    private final UserPersistencePort userPersistencePort;
    private final PostImageApplicationService postImageApplicationService;
    private final UpdateOrderUseCase updateOrderUseCase;
    // -- 공지사항

    /*
    공지사항 생성(관리자)
     */
    @Override
    public PostResponseDto createNotice(UUID userAdminUuid, PostRequestDto.NoticeCreate requestDto) {

        // 관리자 검증
        getValidateAdmin(userAdminUuid);

        // 저장
        PostJpaEntity post = postApplicationService.saveNotice(userAdminUuid, requestDto);

        return postResponseMapper.toResponse(post);
    }

    /*
    리뷰 제외한 기본 포스트(모든 이용자)
    */
    @Override
    public PostResponseDto getPost(UUID postUuid) {

        // 포스트 조회
        PostJpaEntity post = postApplicationService.getPost(postUuid);

        return postResponseMapper.toPostDetailResponse(post);
    }


    /*
    공지사항 업데이트(관리자)
     */
    @Override
    public PostResponseDto updateNotice(UUID userAdminUuid, UUID postUuid, PostUpdateDto.NoticeUpdate updateDto) {

        //관리자 검증
        getValidateAdmin(userAdminUuid);

        //변경
        PostJpaEntity post = postApplicationService.updateNotice(postUuid, updateDto);

        return postResponseMapper.toResponse(post);
    }

    @Override
    public void deleteNotice(UUID userAdminUuid, UUID postUuid) {

        //관리자 검증
        getValidateAdmin(userAdminUuid);

        //삭제
        postApplicationService.deleteNotice(postUuid);
    }

    @Override
    public Page<PostResponseDto> getSearchPostsByAdmin(UUID userAdminUuid, String title, String content, String nickname, PostCategory postCategory, LocalDate startDate, LocalDate endDate, Pageable pageable) {

        //관리자 검증
        getValidateAdmin(userAdminUuid);

        // 유저 닉네임(유니크) 조회
        User user = findUserByNickname(nickname);

        //페이지 조회
        Page<PostJpaEntity> posts = postApplicationService.getSearchPostsByAdmin(title, content, user.getUserUuid(), postCategory, startDate, endDate, pageable);

        return posts.map(postResponseMapper::toResponse);
    }


    // -- 레뷰


    /*
    리뷰 생성
     */
    @Override
    @Transactional
    public PostResponseDto createReview(UUID userUuid, PostRequestDto.ReviewCreate requestDto, MultipartFile image) {

        // 포스트 생성
        PostJpaEntity post = postApplicationService.saveReview(userUuid, requestDto);

        PostImageJpaEntity postImage = null;

        if (image != null && !image.isEmpty()) {
            postImage = postImageApplicationService.saveImage(post, image);
        }

        updateOrderUseCase.changeIsReviewed(userUuid, requestDto.getOrderCode());

        return postResponseMapper.toCreateResponse(post, postImage);
    }

    /*
    리뷰 업데이트(본인)
     */
    @Override
    @Transactional
    public PostResponseDto updateReview(UUID userUuid, UUID postUuid, PostUpdateDto.ReviewUpdate updateDto, MultipartFile image) {

        // 포스트 업데이트
        PostJpaEntity post = postApplicationService.updateReview(userUuid, postUuid, updateDto);


        //포스트 이미지 업데이트
        PostImageJpaEntity postImage = postImageApplicationService.updatePostImage(post, image, updateDto.isImageDeleted());


        return postResponseMapper.toCreateResponse(post, postImage);

    }

    /*
    리뷰 조회(모든 이용자)
     */
    @Override
    public PostResponseDto getReview(UUID postUuid) {
        //리뷰 조회
        PostJpaEntity post = postApplicationService.getPost(postUuid);

        // 해당 리뷰 이미지 조회
        PostImageJpaEntity postImage = postImageApplicationService.getImageByPostId(post.getPostId());

        return postResponseMapper.toReviewDetailResponse(post, postImage);
    }


    /*
    포스트 조회 (공지, 자유게시판)
     */
    @Override
    public Page<PostResponseDto> getSearchPosts(String title, String content, PostCategory postCategory, Pageable pageable) {

        Page<PostJpaEntity> posts = postApplicationService.getSearchPosts(title, content, postCategory, pageable);

        if (posts.isEmpty()) {
            return Page.empty(pageable);
        }

        return posts.map(postResponseMapper::toNoticeListResponse);
    }



    /*
    리뷰 리스트 조회
    */
    @Override
    public Page<PostResponseDto> getSearchReview(UUID userUuid, String title, String content, PostCategory postCategory, Pageable pageable) {

        Page<PostJpaEntity> posts = postApplicationService.getSearchPosts(title, content, postCategory, pageable);

        if (posts.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> postIds = posts.map(PostJpaEntity::getPostId).toList();
        List<UUID> authorUuids = posts.map(PostJpaEntity::getAuthorUuid).toList();

        Map<Long, PostImageJpaEntity> imageMap = postImageApplicationService
                .getImageByPostIds(postIds)
                .stream()
                .collect(Collectors.toMap(
                        image -> image.getPost().getPostId(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));


        Map<UUID, String> nicknameNmap = userPersistencePort
                .findAllByUserUuidIn(authorUuids)
                .stream()
                .collect(Collectors.toMap(
                        User::getUserUuid,
                        User::getNickname
                ));

        return posts.map(post -> {
            PostImageJpaEntity image = imageMap.get(post.getPostId());
            String nickname = nicknameNmap.get(post.getAuthorUuid());

            if (nickname == null) {
                nickname = "알수없음";
            }

            return postResponseMapper.toReviewResponse(post, nickname, image, userUuid);
        });

    }


    /*
    포스트 삭제 (본인만)
     */
    @Override
    public void deleteReview(UUID authorUserUuid, UUID postUuid) {
        postApplicationService.deleteReview(authorUserUuid, postUuid);
    }





    // helper
    private User findUserByUserUuid(UUID userAdminUuid) {
        return userPersistencePort.findByUserUuid(userAdminUuid)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
    }


    private void getValidateAdmin(UUID userAdminUuid) {
        User userAdmin = findUserByUserUuid(userAdminUuid);
        userAdmin.validateAdminRole();
    }


    private User findUserByNickname(String nickname) {
        return userPersistencePort.findByNickname(nickname)
                .orElse(null);
    }
}
