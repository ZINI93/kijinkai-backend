package com.kijinkai.domain.post.service.postimage;

import com.kijinkai.domain.post.entity.PostImageJpaEntity;
import com.kijinkai.domain.post.entity.PostJpaEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface PostImageApplicationService {

    PostImageJpaEntity saveImage(PostJpaEntity post, MultipartFile file);

    PostImageJpaEntity getImageByPostId(Long postId);
    List<PostImageJpaEntity> getImageByPostIds(List<Long> postId);

    PostImageJpaEntity updatePostImage(PostJpaEntity post, MultipartFile file,  boolean isImageDeleted);

}